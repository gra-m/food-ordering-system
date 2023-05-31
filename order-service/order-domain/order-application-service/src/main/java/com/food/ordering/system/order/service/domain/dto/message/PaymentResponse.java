package com.food.ordering.system.order.service.domain.dto.message;

import com.food.ordering.system.domain.valueobject.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponse {
private String id;
private String sagaId;
private String orderId;
private String paymentId;
private String customerId;
private String price;
private String createdAt;
private PaymentStatus paymentStatus;
private List<String> failureMessages;

}
