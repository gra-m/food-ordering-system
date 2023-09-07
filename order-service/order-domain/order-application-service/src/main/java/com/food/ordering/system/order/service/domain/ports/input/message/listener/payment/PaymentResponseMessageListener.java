package com.food.ordering.system.order.service.domain.ports.input.message.listener.payment;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;

/**
 * Ports?: These are nothing but interfaces that need to be implemented with adapters; there are two types of ports in
 * hexagonal architecture input and output.
 * <p>
 * Input ports == Implemented here in the order-application-service
 * whereas output ports are implemented in infrastructure (currently kafka) via order-messaging
 */
public interface PaymentResponseMessageListener
{

    void paymentCompleted(PaymentResponse paymentResponse);

    void paymentCancelled(PaymentResponse paymentResponse);


}
