package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * Generic DomainEvent is being used as a marker class for all domain events, here PaymentEvent accommodates
 * Payment objects -> PaymentCompleted/Cancelled/FailedEvent but this can easily work for
 * Booking objects -> BookingCompleted/Cancelled/FailedEvent
 */
public abstract class PaymentEvent implements DomainEvent<Payment> {

/**
 * By making these uninitialised variables final, a constructor is required in order to initialise them.
 * This enforces the instantiation of these variables within any extending class. So here an abstract class is
 * being used to enforce a template on all inheriting classes.
 */
private final Payment payment;
private final ZonedDateTime createdAt;
private final List<String> failureMessages;


public PaymentEvent(Payment payment, ZonedDateTime createdAt, List<String> failureMessages) {
    this.payment = payment;
    this.createdAt = createdAt;
    this.failureMessages = failureMessages;
}

public Payment getPayment() {
    return payment;
}

public ZonedDateTime getCreatedAt() {
    return createdAt;
}

public List<String> getFailureMessages() {
    return failureMessages;
}


}
