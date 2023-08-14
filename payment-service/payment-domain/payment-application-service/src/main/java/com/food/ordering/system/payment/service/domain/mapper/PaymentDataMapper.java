package com.food.ordering.system.payment.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Given that input Order Data Transfer Objects need to be mapped to domain objects and domain objects need to be
 * mapped to output objects.
 * <p>From a DDD perspective you could consider this the 'Factory' as creation of and conversion of Domain
 * objects and DTO's is delegated to this Mapper Class</p>
 * <a href="https://stackoverflow.com/questions/555241/domain-driven-design-and-the-role-of-the-factory-class">
 * more about factories in Domain Driven design, and links to books...</a>
 */
@Component
public class PaymentDataMapper {

public Payment paymentRequestModelToPayment(PaymentRequest paymentRequest) {
    return Payment
    .builder()
    .orderId(new OrderId(UUID.fromString(paymentRequest.getOrderId())))
    .customerId(new CustomerId(UUID.fromString(paymentRequest.getCustomerId())))
    .price(new Money(paymentRequest.getPrice()))
    .build();
}


}
