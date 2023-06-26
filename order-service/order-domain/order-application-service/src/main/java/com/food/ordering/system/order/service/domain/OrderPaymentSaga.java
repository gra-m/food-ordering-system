package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.EmptyEvent;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {
private final OrderDomainService orderDomainService;
private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;
private final OrderSagaHelper orderSagaHelper;

public OrderPaymentSaga(OrderDomainService orderDomainService,
                        OrderRepository orderRepository,
                        OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher,
                        OrderSagaHelper orderSagaHelper) {
    this.orderDomainService = orderDomainService;
    this.orderPaidRestaurantRequestMessagePublisher = orderPaidRestaurantRequestMessagePublisher;
    this.orderSagaHelper = orderSagaHelper;
}

/**
 * Processing transaction for SAGA step 1 returning the Event that triggers SAGA step 2.
 *
 * @param paymentResponse PaymentResponse data to be processed
 * @return OrderPaidEvent -> The DomainEvent returned after processing
 */
@Override
@Transactional
public OrderPaidEvent process(PaymentResponse paymentResponse) {
    String orderId = paymentResponse.getOrderId();
    log.info("[SAGA1 process payment-response -to-> OrderPaidEvent, pre-save] Completing payment for order with id: {}",
    orderId);

    Order order = orderSagaHelper.findOrder(orderId);
    OrderPaidEvent domainEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);

    orderSagaHelper.saveOrder(order);

    log.info("[SAGA1 process payment-response -to-> OrderPaidEvent post-save] Order with id {} is paid [UUID no " +
    "toString]",
    order.getId().getValue());

    return domainEvent;
}


/**
 * Rollback the action for this SAGA step 1
 *
 * @param paymentResponse PaymentResponseData to be 'rolled back' empty here as this is first step
 * @return EmptyEvent as this is the last rollback in the transaction
 */
@Override
@Transactional
public EmptyEvent rollback(PaymentResponse paymentResponse) {
    String orderId = paymentResponse.getOrderId();
    log.info("[SAGA1 rollback payment-response -to-> EmptyEvent pre-save] Cancelling order with id: {}", orderId );
    Order order = orderSagaHelper.findOrder(orderId);

    orderSagaHelper.saveOrder(order);

    log.info("[SAGA1 rollback payment-response -to-> EmptyEvent post-save] order with id: {} cancelled", orderId);

    return EmptyEvent.INSTANCE;
}


}
