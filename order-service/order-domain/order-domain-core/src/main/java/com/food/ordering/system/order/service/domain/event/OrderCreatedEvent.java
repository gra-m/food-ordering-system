package com.food.ordering.system.order.service.domain.event;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.entity.Order;

import java.time.ZonedDateTime;

/**
 *
 */
public class OrderCreatedEvent extends OrderEvent {

private final DomainEventPublisher<OrderCreatedEvent> orderCreatedPaymentRequestMessagePublisherDomainEventPublisher;

public OrderCreatedEvent(Order order, ZonedDateTime createdAt,
                         DomainEventPublisher<OrderCreatedEvent> orderCreatedPaymentRequestMessagePublisherDomainEventPublisher) {
      super(order, createdAt);
      this.orderCreatedPaymentRequestMessagePublisherDomainEventPublisher = orderCreatedPaymentRequestMessagePublisherDomainEventPublisher;
}

/**
 *
 */
@Override
public void fire() {
      orderCreatedPaymentRequestMessagePublisherDomainEventPublisher.publish(this);

}


}
