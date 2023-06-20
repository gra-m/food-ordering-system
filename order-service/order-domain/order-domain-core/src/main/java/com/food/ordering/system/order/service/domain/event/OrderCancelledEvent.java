package com.food.ordering.system.order.service.domain.event;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

public class OrderCancelledEvent extends OrderEvent {

private final DomainEventPublisher<OrderCancelledEvent> orderCancelledPaymentRequestMessagePublisherDomainEventPublisher;

public OrderCancelledEvent(Order order, ZonedDateTime createdAt,
                           DomainEventPublisher<OrderCancelledEvent> orderCancelledPaymentRequestMessagePublisherDomainEventPublisher) {
      super(order, createdAt);
      this.orderCancelledPaymentRequestMessagePublisherDomainEventPublisher = orderCancelledPaymentRequestMessagePublisherDomainEventPublisher;
}

/**
 *
 */
@Override
public void fire() {
      orderCancelledPaymentRequestMessagePublisherDomainEventPublisher.publish(this);
}


}
