package com.food.ordering.system.order.service.domain.ports.input.message.listener.payment;

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * Adapter.
 *
 * fixme - early note
 * Triggered by other domain events from other bound contexts BeanA [here] to BeanB[@Transactional] in
 * separate helper. SAGA complete once implemented
 */

@Slf4j
@Validated
@Service
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener {

/**
 * @param paymentResponse
 */
@Override
public void paymentCompleted(PaymentResponse paymentResponse) {

}

/**
 * @param paymentResponse
 */
@Override
public void paymentCancelled(PaymentResponse paymentResponse) {

}
}
