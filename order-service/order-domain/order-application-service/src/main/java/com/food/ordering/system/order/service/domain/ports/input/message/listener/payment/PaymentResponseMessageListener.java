package com.food.ordering.system.order.service.domain.ports.input.message.listener.payment;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;

/**
 * A listening port in the domain layer
 */
public interface PaymentResponseMessageListener {

void paymentCompleted(PaymentResponse paymentResponse);

void paymentCancelled(PaymentResponse paymentResponse);
}
