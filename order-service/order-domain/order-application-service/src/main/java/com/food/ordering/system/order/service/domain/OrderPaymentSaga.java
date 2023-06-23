package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.EmptyEvent;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository;
import com.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * SAGA STEP 1
 * T == PaymentResponse as this SagaStep will be called after receiving a Payment response:
 * <p>
 * PaymentResponseKafkListener calls-> PaymentResponseMessageListener/Impl calls-> [this class]
 */
@Slf4j
@Component
public class OrderPaymentSaga implements SagaStep<PaymentResponse, OrderPaidEvent, EmptyEvent> {
private final OrderDomainService orderDomainService;
private final OrderRepository orderRepository;
private final OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher;

public OrderPaymentSaga(OrderDomainService orderDomainService,
                        OrderRepository orderRepository,
                        OrderPaidRestaurantRequestMessagePublisher orderPaidRestaurantRequestMessagePublisher) {
    this.orderDomainService = orderDomainService;
    this.orderRepository = orderRepository;
    this.orderPaidRestaurantRequestMessagePublisher = orderPaidRestaurantRequestMessagePublisher;
}

/**
 * Processing transaction for SAGA step 1
 *
 * @param paymentResponse PaymentResponse data to be processed
 * @return OrderPaidEvent -> The DomainEvent returned after processing
 */
@Override
@Transactional
public OrderPaidEvent process(PaymentResponse paymentResponse) {
    String orderId = paymentResponse.getOrderId();
    log.info("[SAGA pre-save] Completing payment for order with id: {}", orderId);

    Order order = findOrder(orderId);
    OrderPaidEvent domainEvent = orderDomainService.payOrder(order, orderPaidRestaurantRequestMessagePublisher);

    orderRepository.save(order);
    log.info("[SAGA post-save] Order with id {} is paid [UUID no toString]", order.getId().getValue());

    return domainEvent;
}

private Order findOrder(String orderId) {
    Optional<Order> orderResponse = orderRepository.findById(orderId);

    if( orderResponse.isEmpty() ) {
        log.error("Order with id: {} could not be found!", orderId);
        throw new OrderNotFoundException(String.format("order with id %s could not be found!", orderId));
    }
    return orderResponse.get();
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
    log.info("[SAGA pre-save] Cancelling order with id: {}", orderId );
    Order order = findOrder(orderId);

    orderRepository.save(order);
    log.info("[SAGA post-save] order with id: {} cancelled", orderId);

    return EmptyEvent.INSTANCE;
}


}
