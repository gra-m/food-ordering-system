package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.scheduler.approval.ApprovalOutboxHelper;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.saga.SagaStatus;
import com.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstants.UTCBRU;

/**
 * SAGA STEP 2 OrderPaid -> [To RestaurantService] "can order be fulfilled? ->Y/N
 * T == RestaurantApprovalResponse
 */
@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse> {
    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderApprovalSaga(OrderDomainService orderDomainService,
                             OrderSagaHelper orderSagaHelper,
                             PaymentOutboxHelper paymentOutboxHelper,
                             ApprovalOutboxHelper approvalOutboxHelper,
                             OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.orderDataMapper = orderDataMapper;
    }


    /**
     * Processing transaction for SAGA step, SagaStatus is set to SagaStatus.PROCESSING before triggering the Restaurant
     * Approval phase.
     *
     * @param restaurantApprovalResponse the response with the new data to be processed and persisted
     * @return The DomainEvent returned after processing
     */
    @Override
    @Transactional
    public void process(RestaurantApprovalResponse restaurantApprovalResponse) {

        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
                approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(restaurantApprovalResponse.getSagaId()),
                        SagaStatus.PROCESSING);

        boolean restaurantApprovalAlreadyProcessed = orderApprovalOutboxMessageResponse.isEmpty();
        if (restaurantApprovalAlreadyProcessed) {
            log.info(String.format("An outbox message with saga id: {} is already processed!", restaurantApprovalResponse.getSagaId()));
            return;
        }

        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();
        Order order = approveOrder(restaurantApprovalResponse);
        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage, order.getOrderStatus(), sagaStatus));

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(restaurantApprovalResponse.getSagaId(), order.getOrderStatus(),
                sagaStatus));

        log.info("Order with id: {} is approved", order.getId().getValue());

    }




    /**
     * todo WIP, once tested and working, get all steps into easy to follow descriptive methods so easy to follow when looking back at this.
     * The restaurant cannot honour/ the order is the data in this restaurantApprovalResponse it needs to be rolled back, meaning that:
     * The customer needs their money back - a new outbox message for this journey has to be started (a new PaymentOutboxMessage)
     *
     * @param restaurantApprovalResponse the data to be 'rolled back'
     * @return the Domain event returned after the rollback.
     */
    @Override
    @Transactional
    public void rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
        // 01 CHECK1 not already rollbacked by reading the approval outbox message object from the database, if it does not exist it has already been rollbacked.
        // It will not exist to a second threads request for this object as:  rollbackOrder -> cancelOrderPayment -> order.initCancel() sets order status to CANCELLING,
        // so after 02 SagaStatus will not be @PROCESSING but @COMPENSATING for a subsequent thread call leading to an empty optional below:
        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
                approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(restaurantApprovalResponse.getSagaId()),
        SagaStatus.PROCESSING);

        boolean rollbackAlreadyCompleted = orderApprovalOutboxMessageResponse.isEmpty();

        if( rollbackAlreadyCompleted ) {
            log.info(String.format("An outbox message with saga id: {} is already roll backed!", restaurantApprovalResponse.getSagaId()));
            return;
        }


        OrderApprovalOutboxMessage orderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();
        OrderCancelledEvent domainEvent = rollbackOrder(restaurantApprovalResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());

        // 02 Save event that locks once only as sagaStatus from here on within outbox db will be @COMPENSATING
        // BUT what if two threads got here at the same time?
        // CHECK2 Optimistic locking is enforced with the use of @Version private int version; in ApprovalOutboxEntity. version is incremented with every update.
        // Both threads will be starting their operations with say version 0 of this object that they want to update, but only ONE can ever have that object
        // still have the same version number when it comes to update, as each update increments version number. If version numbers do not match, the persistence
        // provider will throw an OptimisticLockingException and the changes of the second subsequent thread will be rollbacked.
       approvalOutboxHelper.save(getUpdatedApprovalOutboxMessage(orderApprovalOutboxMessage, domainEvent.getOrder().getOrderStatus(), sagaStatus));

       // Finally, CHECK3 UNIQUE index on type, saga_id and saga_status, makes it impossible to add the same data into the Outbox message table.
       paymentOutboxHelper.savePaymentOutboxMessage(orderDataMapper.orderCancelledEventToOrderPaymentEventPayload(domainEvent),
               domainEvent.getOrder().getOrderStatus(),
               sagaStatus,
               OutboxStatus.STARTED,
               UUID.fromString(restaurantApprovalResponse.getSagaId()));

        log.info("Order with id: {} is " + "cancelled", domainEvent.getOrder().getId().getValue());
    }



    private Order approveOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Approving order with id: {}", restaurantApprovalResponse.getOrderId());

        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());

        orderDomainService.approveOrder(order);
        orderSagaHelper.saveOrder(order);
        return order;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(OrderApprovalOutboxMessage updatingOrderApprovalOutboxMessage,
                                                                       OrderStatus orderStatus,
                                                                       SagaStatus sagaStatus) {
        updatingOrderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTCBRU)));
        updatingOrderApprovalOutboxMessage.setOrderStatus(orderStatus);
        updatingOrderApprovalOutboxMessage.setSagaStatus(sagaStatus);
        return updatingOrderApprovalOutboxMessage;
    }

    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(sagaId), SagaStatus.PROCESSING);

        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            throw new OrderDomainException(String.format("Payment outbox message cannot be found in %s state", SagaStatus.PROCESSING.name()));
        }

        OrderPaymentOutboxMessage updatingOrderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();
        updatingOrderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTCBRU)));
        updatingOrderPaymentOutboxMessage.setOrderStatus(orderStatus);
        updatingOrderPaymentOutboxMessage.setSagaStatus(sagaStatus);

        return updatingOrderPaymentOutboxMessage;
    }

    private OrderCancelledEvent rollbackOrder(RestaurantApprovalResponse restaurantApprovalResponse) {
        log.info("Canncelling order with " + "id: {}", restaurantApprovalResponse.getOrderId());
        Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
        OrderCancelledEvent domainEvent = orderDomainService.cancelOrderPayment(order, restaurantApprovalResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return domainEvent;
    }
}
