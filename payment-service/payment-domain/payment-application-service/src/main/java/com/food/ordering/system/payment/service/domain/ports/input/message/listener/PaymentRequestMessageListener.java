package com.food.ordering.system.payment.service.domain.ports.input.message.listener;

import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
/**
 * Ports?: These are nothing but interfaces that need to be implemented with adapters; there are two types of ports in
 * hexagonal architecture input and output.
 *
 * Input ports == Implemented in PaymentRequestMessageListenerImpl the payment-application-service
 * whereas output ports are implemented in infrastructure (currently kafka) via payment-messaging
 */
public interface PaymentRequestMessageListener {

void completePayment(PaymentRequest paymentRequest);

void cancelPayment(PaymentRequest paymentRequest);


}
