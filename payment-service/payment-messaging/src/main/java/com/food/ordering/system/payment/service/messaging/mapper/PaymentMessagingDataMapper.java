package com.food.ordering.system.payment.service.messaging.mapper;

import com.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import org.springframework.stereotype.Component;

import java.util.UUID;
/**
 * Injected and used where domain and messaging objects need to be converted.
 */
@Component
public class PaymentMessagingDataMapper {

public PaymentResponseAvroModel paymentCompletedEventToPaymentResponseAvroModel(PaymentCompletedEvent paymentCompletedEvent) {

    return PaymentResponseAvroModel
    .newBuilder()
    .setId(UUID.randomUUID().toString())
    .setSagaId("")
    .setPaymentId(paymentCompletedEvent.getPayment().getId().getValue().toString())
    .setCustomerId(paymentCompletedEvent.getPayment().getCustomerId().getValue().toString())
    .setOrderId(paymentCompletedEvent.getPayment().getOrderId().getValue().toString())
    .setPrice(paymentCompletedEvent.getPayment().getPrice().getAmount())
    .setCreatedAt(paymentCompletedEvent.getCreatedAt().toInstant())
    .setPaymentStatus(PaymentStatus.valueOf(paymentCompletedEvent.getPayment().getPaymentStatus().name()))
    .setFailureMessages(paymentCompletedEvent.getFailureMessages())
    .build();
}

public PaymentResponseAvroModel paymentCancelledEventToPaymentResponseAvroModel(PaymentCancelledEvent paymentCancelledEvent) {
    return PaymentResponseAvroModel
    .newBuilder()
    .setId(UUID.randomUUID().toString())
    .setSagaId("")
    .setPaymentId(paymentCancelledEvent.getPayment().getId().getValue().toString())
    .setCustomerId(paymentCancelledEvent.getPayment().getCustomerId().getValue().toString())
    .setOrderId(paymentCancelledEvent.getPayment().getOrderId().getValue().toString())
    .setPrice(paymentCancelledEvent.getPayment().getPrice().getAmount())
    .setCreatedAt(paymentCancelledEvent.getCreatedAt().toInstant())
    .setPaymentStatus(PaymentStatus.valueOf(paymentCancelledEvent.getPayment().getPaymentStatus().name()))
    .setFailureMessages(paymentCancelledEvent.getFailureMessages())
    .build();
}


public PaymentResponseAvroModel paymentFailedEventToPaymentResponseAvroModel(PaymentFailedEvent paymentFailedEvent) {
    return PaymentResponseAvroModel
    .newBuilder()
    .setId(UUID.randomUUID().toString())
    .setSagaId("")
    .setPaymentId(paymentFailedEvent.getPayment().getId().getValue().toString())
    .setCustomerId(paymentFailedEvent.getPayment().getCustomerId().getValue().toString())
    .setOrderId(paymentFailedEvent.getPayment().getOrderId().getValue().toString())
    .setPrice(paymentFailedEvent.getPayment().getPrice().getAmount())
    .setCreatedAt(paymentFailedEvent.getCreatedAt().toInstant())
    .setPaymentStatus(PaymentStatus.valueOf(paymentFailedEvent.getPayment().getPaymentStatus().name()))
    .setFailureMessages(paymentFailedEvent.getFailureMessages())
    .build();
}

/**
 * To send data to domain layer payment-domain/payment-application-service it must be converted to a domain 'entity'
 * == The domain layer does not need to know about AvroModels.
 *
 * @param paymentRequestAvroModel the payment request received from Kafka
 * @return PaymentRequest a payment request in the form the domain layer needs.
 */
public PaymentRequest paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel) {
    return PaymentRequest
    .builder()
    .id(paymentRequestAvroModel.getId())
    .sagaId(paymentRequestAvroModel.getSagaId())
    .customerId(paymentRequestAvroModel.getCustomerId())
    .orderId(paymentRequestAvroModel.getOrderId())
    .price(paymentRequestAvroModel.getPrice())
    .createdAt(paymentRequestAvroModel.getCreatedAt())
    .paymentOrderStatus(PaymentOrderStatus.valueOf(paymentRequestAvroModel.getPaymentOrderStatus().name()))
    .build();
}


}
