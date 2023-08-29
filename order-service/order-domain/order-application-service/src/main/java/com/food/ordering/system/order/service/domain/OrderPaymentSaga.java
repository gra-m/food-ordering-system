package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage;
import com.food.ordering.system.order.service.domain.outbox.scheduler.payment.PaymentOutboxHelper;
import com.food.ordering.system.saga.SagaStatus;
import com.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.management.openmbean.OpenDataException;
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

public OrderPaymentSaga(OrderDomainService orderDomainService,
                        OrderSagaHelper orderSagaHelper,
                        PaymentOutboxHelper paymentOutboxHelper) {
    this.orderDomainService = orderDomainService;
    this.orderSagaHelper = orderSagaHelper;
    this.paymentOutboxHelper = paymentOutboxHelper;
}

/**
 * Called from ListenerImpl classes for payment and restaurant approval, so it is called after a response is received from
 * the kafka payment response topic used in PaymentResponseKafkaListener and...
 * THIS MEANS an outbox message is already available, to read it from the database PaymentOutboxHelper is used.
 *
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

    log.info("Completing payment for order with id: {}", paymentResponse.getOrderId());

    Order order = orderSagaHelper.findOrder(paymentResponse.getOrderId());
    OrderPaidEvent domainEvent = orderDomainService.payOrder(order);
    orderSagaHelper.saveOrder(order);

    SagaStatus sagaStatus = orderSagaHelper.orderStatusToSagaStatus(domainEvent.getOrder().getOrderStatus());

    paymentOutboxHelper.save(getUpdatedPaymentOutboxMessage(orderPaymentOutboxMessage, domainEvent.getOrder().getOrderStatus(), sagaStatus));

    log.info("Order with id {} is paid", order.getId().getValue());
}

    /**
     * To update local retrieve orderPayementOutboxMessage with new status and time-stamp of this action.
     * @param orderPaymentOutboxMessage
     * @param orderStatus the orderStatus retrieved from Domain event
     * @param sagaStatus  new orderStatus retrieved from DomainEvent translated with orderSagaHelper
     * @return
     */
    private OrderPaymentOutboxMessage getUpdatedPaymentOutboxMessage(OrderPaymentOutboxMessage orderPaymentOutboxMessage, OrderStatus orderStatus, SagaStatus sagaStatus) {

    orderPaymentOutboxMessage.setProcessedAt(ZonedDateTime.now(ZoneId.of(UTCBRU)));
    orderPaymentOutboxMessage.setOrderStatus(orderStatus);
    orderPaymentOutboxMessage.setSagaStatus(sagaStatus);

    return orderPaymentOutboxMessage;
    }


    /**
 * Rollback the action for this SAGA step 1
 *
 * @param paymentResponse PaymentResponseData to be 'rolled back' empty here as this is first step
 * @return EmptyEvent as this is the last rollback in the transaction
 */
@Override
@Transactional
public void rollback(PaymentResponse paymentResponse) {
    String orderId = paymentResponse.getOrderId();
    log.info("[SAGA1 rollback payment-response -to-> EmptyEvent pre-save] Cancelling order with id: {}", orderId);
    Order order = orderSagaHelper.findOrder(orderId);
    orderDomainService.cancelOrder(order, paymentResponse.getFailureMessages());
    orderSagaHelper.saveOrder(order);

    log.info("[SAGA1 rollback payment-response -to-> EmptyEvent post-save] order with id: {} cancelled", orderId);

}


}
