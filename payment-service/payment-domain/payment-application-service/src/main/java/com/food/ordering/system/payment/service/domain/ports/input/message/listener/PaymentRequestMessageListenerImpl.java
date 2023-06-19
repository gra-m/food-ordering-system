package com.food.ordering.system.payment.service.domain.ports.input.message.listener;

import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentRequestMessageListenerImpl implements PaymentRequestMessageListener{

/**
 * @param paymentRequest
 */
@Override
public void completePayment(PaymentRequest paymentRequest) {

}

/**
 * @param paymentRequest
 */
@Override
public void cancelPayment(PaymentRequest paymentRequest) {

}


}
