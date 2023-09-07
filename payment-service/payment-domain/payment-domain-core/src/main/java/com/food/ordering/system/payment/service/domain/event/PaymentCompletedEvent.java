package com.food.ordering.system.payment.service.domain.event;

import com.food.ordering.system.payment.service.domain.entity.Payment;

import java.time.ZonedDateTime;
import java.util.Collections;

public class PaymentCompletedEvent extends PaymentEvent
{


    /**
     * Given that a fresh PaymentEvent has an <i>empty</i> list of failureMessages. The existence of a failure
     * message is
     * a trigger for non-completion.
     *
     * @param payment   The Payment belonging to this PaymentEvent
     *                  Completed/Cancelled/Failed
     * @param createdAt The time that this PaymentEvent was created at ZoneDateTime
     *                  dependency to domain-core.
     */
    public PaymentCompletedEvent(Payment payment,
                                 ZonedDateTime createdAt)
    {
        super(payment, createdAt, Collections.emptyList());
    }


}
