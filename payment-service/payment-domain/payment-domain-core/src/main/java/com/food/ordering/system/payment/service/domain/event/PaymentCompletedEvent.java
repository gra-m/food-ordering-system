package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCompletedEvent extends PaymentEvent {

/**
 * We gain access to common parent interface rather than importing a dependency on  PaymentCompletedEventPublisher
 */
private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher;


/**
 * Given that a fresh PaymentEvent has an <i>empty</i> list of failureMessages. The existence of a failure message is
 * a trigger for non-completion.
 *
 * @param payment                                   The Payment belonging to this PaymentEvent
 *                                                  Completed/Cancelled/Failed
 * @param createdAt                                 The time that this PaymentEvent was created at ZoneDateTime
 * @param paymentCompletedEventDomainEventPublisher Enables PaymentCompletedEvent to 'self-fire' without adding unwanted
 *                                                  dependency to domain-core.
 */
public PaymentCompletedEvent(Payment payment,
                             ZonedDateTime createdAt,
                             DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher) {
    super(payment, createdAt, Collections.emptyList());
    this.paymentCompletedEventDomainEventPublisher = paymentCompletedEventDomainEventPublisher;
}

@Override
public void fire() {
    paymentCompletedEventDomainEventPublisher.publish(this);
}


}
