package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.EmptyEvent;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.saga.SagaStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SAGA STEP 2 OrderPaid -> [To RestaurantService] "can order be fulfilled? ->Y/N
 * T == RestaurantApprovalResponse
 */
@Slf4j
@Component
public class OrderApprovalSaga implements SagaStep<RestaurantApprovalResponse, EmptyEvent, OrderCancelledEvent> {
private final OrderDomainService orderDomainService;
private final OrderSagaHelper orderSagaHelper;
private final OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher;

public OrderApprovalSaga(OrderDomainService orderDomainService,
                         OrderSagaHelper orderSagaHelper,
                         OrderCancelledPaymentRequestMessagePublisher orderCancelledPaymentRequestMessagePublisher) {
    this.orderDomainService = orderDomainService;
    this.orderSagaHelper = orderSagaHelper;
    this.orderCancelledPaymentRequestMessagePublisher = orderCancelledPaymentRequestMessagePublisher;
}


/**
 * Processing transaction for SAGA step
 *
 * @param restaurantApprovalResponse the response with the new data to be processed and persisted
 * @return The DomainEvent returned after processing
 */
@Override
@Transactional
public EmptyEvent process(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("[SAGA2 process restaurantApprovalResponse -to-> EmptyEvent, pre-save] Approving order with id: {}",
    restaurantApprovalResponse.getOrderId());

    Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
    orderDomainService.approveOrder(order);

    orderSagaHelper.saveOrder(order);

    log.info("[SAGA2 process restaurantApprovalResponse -to-> EmptyEvent, post-save] Order with id: {} is approved",
    restaurantApprovalResponse.getOrderId());

    return EmptyEvent.INSTANCE;

}

/**
 * Rollback the action for this SAGA step
 *
 * @param restaurantApprovalResponse the data to be 'rolled back'
 * @return the Domain event returned after the rollback.
 */
@Override
@Transactional
public OrderCancelledEvent rollback(RestaurantApprovalResponse restaurantApprovalResponse) {
    log.info("[SAGA2 rollback restaurantApprovalResponse -to-> OrderCancelledEvent, pre-save] cancelling order with " + "id: {}",
    restaurantApprovalResponse.getOrderId());

    Order order = orderSagaHelper.findOrder(restaurantApprovalResponse.getOrderId());
    OrderCancelledEvent domainEvent = orderDomainService.cancelOrderPayment(order,
    restaurantApprovalResponse.getFailureMessages(),
    orderCancelledPaymentRequestMessagePublisher);

    orderSagaHelper.saveOrder(order);
    log.info("[SAGA2 rollback restaurantApprovalResponse -to-> OrderCancelledEvent, post-save] Order with id: {} is "
    + "cancelled", order.getId().getValue());
    return domainEvent;
}


}
