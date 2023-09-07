package com.food.ordering.system.order.service.domain.ports.input.message.listener.payment;

import com.food.ordering.system.order.service.domain.OrderPaymentSaga;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;

@Slf4j
@Validated
@Service
public class PaymentResponseMessageListenerImpl implements PaymentResponseMessageListener
{
    private final OrderPaymentSaga orderPaymentSaga;

    public PaymentResponseMessageListenerImpl(OrderPaymentSaga orderPaymentSaga)
    {
        this.orderPaymentSaga = orderPaymentSaga;
    }

    /**
     * @param paymentResponse
     */
    @Override
    public void paymentCompleted(PaymentResponse paymentResponse)
    {
        orderPaymentSaga.process(paymentResponse);
        log.info("Order payetn Saga process operation is completed for order id: {}", paymentResponse.getOrderId());
    }

    /**
     * @param paymentResponse
     */
    @Override
    public void paymentCancelled(PaymentResponse paymentResponse)
    {
        orderPaymentSaga.rollback(paymentResponse);
        log.info("Order is roll backed for order id: {} with failure messages: {}",
                paymentResponse.getOrderId(),
                String.join(FAILURE_MESSAGE_DELIMITER, paymentResponse.getFailureMessages()));
    }


}
