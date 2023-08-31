package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
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
 * SAGA STEP 1 OrderCreated -> [to PaymentService] "is order paid?" ->  Y/N
 * T == PaymentResponse, the published response that has been consumed from Kafka, the contents of which will
 * decide whether this step is processed or rolled back.
 * NOTE: process and rollback are essentially the same process in this first step.
 * S == OrderPaidEvent the Saga process returned after saving the order in its new state (only if the order is PAID).
 * U == EmptyEvent, there is no need for a meaningful roll-back event as this is the first SAGA step
 */
@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse> {
    private final OrderDomainService orderDomainService;
    private final OrderSagaHelper orderSagaHelper;
    private final PaymentOutboxHelper paymentOutboxHelper;
    private final ApprovalOutboxHelper approvalOutboxHelper;
    private final OrderDataMapper orderDataMapper;

    public OrderPaymentSaga(OrderDomainService orderDomainService,
                            OrderSagaHelper orderSagaHelper,
                            PaymentOutboxHelper paymentOutboxHelper,
                            ApprovalOutboxHelper approvalOutboxHelper, OrderDataMapper orderDataMapper) {
        this.orderDomainService = orderDomainService;
        this.orderSagaHelper = orderSagaHelper;
        this.paymentOutboxHelper = paymentOutboxHelper;
        this.approvalOutboxHelper = approvalOutboxHelper;
        this.orderDataMapper = orderDataMapper;
    }

    /**
     * Called from ListenerImpl classes for payment and restaurant approval, so it is called after a response is received from
     * the kafka payment response topic used in PaymentResponseKafkaListener and...
     * THIS MEANS an outbox message is already available, to read it from the database PaymentOutboxHelper is used.
     * <p>
     * Restaurant Approval equivalent = restaurant approval topics/RestaurantApprovalRequestKafkaListener
     *
     * @param paymentResponse PaymentResponse data to be processed
     * @return OrderPaidEvent -> The DomainEvent returned after processing
     */
    @Override
    @Transactional
    public void process(PaymentResponse paymentResponse) {
        // Retrieve pending == started order
        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(UUID.fromString(paymentResponse.getSagaId()), SagaStatus.STARTED);

    /* if empty it has already been processed and this is an erroneous subsequent call to process()
    1. could happen when order-service sends the same outbox message to the payment service,
    2. which could happen if the scheduler service runs twice before outbox catches up (System Issue).
    3. which could happen when you have multiple instances of the order service.
    SO checking SagaStatus.Started catches this edge-case
    */
        if (orderPaymentOutboxMessageResponse.isEmpty()) {
            log.info("An outbox message with saga id: {} is already processed!", paymentResponse.getSagaId());
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();


        OrderPaidEvent domainEvent = completePaymentForOrder(paymentResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage, domainEvent.getOrder().getOrderStatus(), sagaStatus));

        // Now with outbox, event is no longer fired, local OUTBOX db is updated.

        approvalOutboxHelper.saveApprovalOutboxMessage(orderDataMapper.OrderPaidEventToOrderApprovalEventPayload(domainEvent), domainEvent.getOrder().getOrderStatus(),
                sagaStatus,
                OutboxStatus.STARTED,
                UUID.randomUUID());

        log.info("Order with id {} is paid", domainEvent.getOrder().getId().getValue());
    }

    /**
     * To update local retrieve orderPayementOutboxMessage with new status and time-stamp of this action.
     *
     * @param orderPaymentOutboxMessage
     * @param orderStatus               the orderStatus retrieved from Domain event
     * @param sagaStatus                new orderStatus retrieved from DomainEvent translated with orderSagaHelper
     * @return
     */
    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OrderStatus orderStatus, SagaStatus sagaStatus) {

        orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTCBRU)));
        orderPaymentOutboxMessage.setOrderStatus(orderStatus);
        orderPaymentOutboxMessage.setSagaStatus(sagaStatus);

        return orderPaymentOutboxMessage;
    }


    /**
     * Can be called if payment is COMPLETED, FAILED or CANCELLED
     *
     * @param paymentResponse PaymentResponseData to be 'rolled back' empty here as this is first step
     * @return EmptyEvent as this is the last rollback in the transaction
     */
    @Override
    @Transactional
    public void rollback(PaymentResponse paymentResponse) {

        Optional<OrderPaymentOutboxMessage> orderPaymentOutboxMessageResponse =
                paymentOutboxHelper.getPaymentOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(paymentResponse.getSagaId()),
                        getCurrentSagaStatus(paymentResponse.getPaymentStatus()));

        boolean rollbackAlreadyCompleted = orderPaymentOutboxMessageResponse.isEmpty();

        if (rollbackAlreadyCompleted) {
            log.info(String.format("An outbox message with saga id: {} is already roll backed!", paymentResponse.getSagaId()));
            return;
        }

        OrderPaymentOutboxMessage orderPaymentOutboxMessage = orderPaymentOutboxMessageResponse.get();
        Order order = rollbackPaymentForOrder(paymentResponse);

        SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(order.getOrderStatus());

        paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage, order.getOrderStatus(), sagaStatus));

        boolean cancelledUpdatedApprovalOutboxMessageNeedsToBeCreatedAndSaved = paymentResponse.getPaymentStatus() == PaymentStatus.CANCELLED;
        if (cancelledUpdatedApprovalOutboxMessageNeedsToBeCreatedAndSaved) {
            OrderApprovalOutboxMessage updatedToCancelledApprovalOutboxMessage = getUpdatedApprovalOutboxMessage(paymentResponse.getSagaId(), order.getOrderStatus(), sagaStatus);
            approvalOutboxHelper.save(updatedToCancelledApprovalOutboxMessage);
        }

        log.info("Order with id: {} cancelled", order.getId());

    }




    private SagaStatus[] getCurrentSagaStatus(PaymentStatus paymentStatus) {
        return switch (paymentStatus) {
            case COMPLETED -> new SagaStatus[]  {SagaStatus.STARTED}; //When payment service is triggered for 1st time saga has just started when returned, it is still in the same state
            case CANCELLED -> new SagaStatus[] {SagaStatus.PROCESSING}; //When payment service is contacted to cancel and rollback a payment order is in middle of SagaProcessing with state 'Processing'
            case FAILED -> new SagaStatus[] {SagaStatus.STARTED, SagaStatus.PROCESSING};
        };
    }

    private OrderPaidEvent completePaymentForOrder(PaymentResponse paymentResponse){
        log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());

        Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
        OrderPaidEvent orderPaidEvent = orderDomainService.payOrder(order);
        orderSagaHelper.saveOrder(order);

        return orderPaidEvent;
    }

    private Order rollbackPaymentForOrder(PaymentResponse paymentResponse) {
        String orderId = paymentResponse.getOrderId();
        log.info("Cancelling order with id: {}", orderId);
        Order order = orderSagaHelper.findOrder(orderId);
        orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
        orderSagaHelper.saveOrder(order);
        return order;
    }

    private OrderApprovalOutboxMessage getUpdatedApprovalOutboxMessage(String sagaId, OrderStatus orderStatus, SagaStatus sagaStatus) {
        Optional<OrderApprovalOutboxMessage> orderApprovalOutboxMessageResponse =
                approvalOutboxHelper.getApprovalOutboxMessageBySagaIdAndSagaStatus(
                        UUID.fromString(sagaId),
                        sagaStatus
                );

        if (orderApprovalOutboxMessageResponse.isEmpty()) {
            throw new OrderDomainException(String.format("Approval outbox message could not be found in %s status", SagaStatus.COMPENSATING.name()));
        }
        OrderApprovalOutboxMessage updatingOrderApprovalOutboxMessage = orderApprovalOutboxMessageResponse.get();
        updatingOrderApprovalOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTCBRU)));
        updatingOrderApprovalOutboxMessage.setOrderStatus(orderStatus);
        updatingOrderApprovalOutboxMessage.setSagaStatus(sagaStatus);

        return updatingOrderApprovalOutboxMessage;
    }

}
