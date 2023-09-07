package com.food.ordering.system.payment.service.domain.dto;

import com.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Given Lombok annotations make things easier. and @AllArgsConstructor is required for @Builder.
 * <p>Given that a paymentRequest must be received before a payment Response can be created
 * {@link com.food.ordering.system.order.service.domain.dto.message}
 * PaymentResponse} </p>
 */
@Getter
@Builder
@AllArgsConstructor
public class PaymentRequest
{
    private String id;
    private String sagaId;
    private String orderId;
    private String customerId;
    private BigDecimal price;
    private Instant createdAt;
    private PaymentOrderStatus paymentOrderStatus;

    /**
     * Required to set the PaymentOrderStatus based on the outcome of the business logic in payment-domain module
     *
     * @param paymentOrderStatus
     */
    public void setPaymentOrderStatus(PaymentOrderStatus paymentOrderStatus)
    {
        this.paymentOrderStatus = paymentOrderStatus;
    }


}
