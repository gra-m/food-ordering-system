package com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;

/**
 * Ports?: These are nothing but interfaces that need to be implemented with adapters; there are two types of ports in
 * hexagonal architecture input and output.
 * <p>
 * Note: Long but meaningful name making it clear why this publisher is being used..
 * <p>
 * Output ports == Implemented in: infrastructure modules via messaging module (order-messaging)
 */
public interface OrderCancelledPaymentRequestMessagePublisher extends DomainEventPublisher<OrderCancelledEvent> {
}
