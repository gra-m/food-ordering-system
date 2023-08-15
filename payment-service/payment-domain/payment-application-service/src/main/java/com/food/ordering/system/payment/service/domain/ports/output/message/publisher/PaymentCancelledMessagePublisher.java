package com.food.ordering.system.payment.service.domain.ports.output.message.publisher;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;

/**
 * Ports?: These are nothing but interfaces that need to be implemented with adapters; there are two types of ports in
 * hexagonal architecture input and output.
 * <p>
 * This message publisher is an 'Output Port' in terms of Hexagonal Architecture.
 * <p>
 * Output ports == Implemented in: infrastructure modules via messaging module (payment-messaging)
 */
public interface PaymentCancelledMessagePublisher extends DomainEventPublisher<PaymentCancelledEvent> {

}
