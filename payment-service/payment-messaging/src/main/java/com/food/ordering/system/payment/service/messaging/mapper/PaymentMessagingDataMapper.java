package com.food.ordering.system.payment.service.messaging.mapper;

import com.food.ordering.system.domain.valueobject.PaymentOrderStatus;
import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Injected and used where domain and messaging objects need to be converted.
 */
@Component
public class PaymentMessagingDataMapper
{

    public PaymentResponseAvroModel orderEvenPayloadToPaymentResponseAvroModel(String sagaId,
                                                                               OrderEventPayload orderEventPayload)
    {
        return PaymentResponseAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setSagaId(sagaId)
                .setPaymentId(orderEventPayload.getPaymentId())
                .setCustomerId(orderEventPayload.getCustomerId())
                .setOrderId(orderEventPayload.getOrderId())
                .setPrice(orderEventPayload.getPrice())
                .setCreatedAt(orderEventPayload.getCreatedAt().toInstant())
                .setPaymentStatus(PaymentStatus.valueOf(orderEventPayload.getPaymentStatus()))
                .setFailureMessages(orderEventPayload.getFailureMessages())
                .build();
    }

    /**
     * To send data to domain layer payment-domain/payment-application-service it must be converted to a domain 'entity'
     * == The domain layer does not need to know about AvroModels.
     *
     * @param paymentRequestAvroModel the payment request received from Kafka
     * @return PaymentRequest a payment request in the form the domain layer needs.
     */
    public PaymentRequest paymentRequestAvroModelToPaymentRequest(PaymentRequestAvroModel paymentRequestAvroModel)
    {
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
