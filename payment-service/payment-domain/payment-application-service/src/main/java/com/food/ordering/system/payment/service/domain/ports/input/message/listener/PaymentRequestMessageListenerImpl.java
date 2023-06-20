package com.food.ordering.system.payment.service.domain.ports.input.message.listener;

import com.food.ordering.system.payment.service.domain.PaymentRequestHelper;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Listens for incoming PaymentRequests
 */
@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

private final PaymentRequestHelper paymentRequestHelper;


public PaymentRequestMessageListenerImpl(PaymentRequestHelper paymentRequestHelper){
    this.paymentRequestHelper = paymentRequestHelper;
}


/** Given that the system requires an attempt to complete a payment send a payment request to the payment request
 * helper and receive a paymentEvent back
 * @param paymentRequest the request that has been passed for completion
 */
@Override
public void completePayment(PaymentRequest paymentRequest) {
    PaymentEvent paymentEvent = paymentRequestHelper.persistPayment(paymentRequest);
    fireEvent(paymentEvent);

}

/** Given that the system requires an attempt to cancel a payment, send a payment request to paymentRequestHelper and
 * receive a paymentEvent back
 * @param paymentRequest the request that has been passed for cancellation
 */
@Override
public void cancelPayment(PaymentRequest paymentRequest) {
    PaymentEvent paymentEvent = paymentRequestHelper.persistCancelPayment(paymentRequest);
    fireEvent(paymentEvent);
}

/**
 * Note: refactored so paymentEvent is 'self-firing' negating need to check for type here. All new events should be
 * implemented in the same way see commits 49-49b for detail.
 *
 * @param paymentEvent the paymentEvent received back from internal business logic.
 */
private void fireEvent(PaymentEvent paymentEvent) {
    log.info("Publishing payment event with payment id: {} and order id: {}",
    paymentEvent.getPayment().getId().getValue(),
    paymentEvent.getPayment().getOrderId().getValue());

    paymentEvent.fire();

    }
}


