package com.food.ordering.system.payment.service.domain.ports.input.message.listener;

import com.food.ordering.system.payment.service.domain.PaymentRequestHelper;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCancelledMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentCompletedMessagePublisher;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentFailedMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Listens for incoming PaymentRequests
 */
@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener {

private final PaymentRequestHelper paymentRequestHelper;
private final PaymentCompletedMessagePublisher paymentCompletedMessagePublisher;
private final PaymentCancelledMessagePublisher paymentCancelledMessagePublisher;
private final PaymentFailedMessagePublisher paymentFailedMessagePublisher;

public PaymentRequestMessageListenerImpl(PaymentRequestHelper paymentRequestHelper,
                                         PaymentCompletedMessagePublisher paymentCompletedMessagePublisher,
                                         PaymentCancelledMessagePublisher paymentCancelledMessagePublisher,
                                         PaymentFailedMessagePublisher paymentFailedMessagePublisher) {
    this.paymentRequestHelper = paymentRequestHelper;
    this.paymentCompletedMessagePublisher = paymentCompletedMessagePublisher;
    this.paymentCancelledMessagePublisher = paymentCancelledMessagePublisher;
    this.paymentFailedMessagePublisher = paymentFailedMessagePublisher;
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
 * Given that a paymentEvent has been created by the business logic of paymentRequestHelper publish an event of the
 * correct kind.
 *
 * @param paymentEvent the paymentEvent received back from internal business logic.
 */
private void fireEvent(PaymentEvent paymentEvent) {
    log.info("Publishing payment event with payment id: {} and order id: {}",
    paymentEvent.getPayment().getId().getValue(),
    paymentEvent.getPayment().getOrderId().getValue());

    paymentEvent.fire();

     if( paymentEvent instanceof PaymentCancelledEvent ) {
        paymentCancelledMessagePublisher.publish(( PaymentCancelledEvent ) paymentEvent);
    } else if( paymentEvent instanceof PaymentFailedEvent ) {
        paymentFailedMessagePublisher.publish(( PaymentFailedEvent ) paymentEvent);
    }
}


}
