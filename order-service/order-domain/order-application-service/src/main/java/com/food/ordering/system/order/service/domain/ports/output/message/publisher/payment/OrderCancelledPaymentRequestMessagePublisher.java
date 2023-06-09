package com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;

/**
 * Long but meaningful name making it clear why this publisher is being used..
 */
public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
