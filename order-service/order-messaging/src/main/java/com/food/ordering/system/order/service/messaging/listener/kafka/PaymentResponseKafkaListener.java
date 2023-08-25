package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * Listens for Kafka messages regarding payment and triggers adapter implementaion PaymentResponseMessageListerImpl.
 */
@Slf4j
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

private final PaymentResponseMessageListener paymentResponseMessageListener;
private final OrderMessagingDataMapper orderMessagingDataMapper;

public PaymentResponseKafkaListener(PaymentResponseMessageListener paymentResponseMessageListener,
                                    OrderMessagingDataMapper orderMessagingDataMapper) {
    this.paymentResponseMessageListener = paymentResponseMessageListener;
    this.orderMessagingDataMapper = orderMessagingDataMapper;
}

/**
 * Differentiates between different actions based on whether paymentResponseAvroModel received is COMPLETED vs
 * CANCELLED or FAILED. Note, the different Enum types (one created automatically from the .avcs model) cannot be
 * directly compared ==.
 * <p>
 * note: @KafkaListener makes a kafka listener of a simple method with a single annotation, id and topics set with
 * config.
 * <p>
 * Spring method param annotations @Payload and @Header set the payload and headers to be as expected for Kafka
 *
 * @param messages   A list of PaymentResponseAvroModel messages to be consumed
 * @param keys       A list of Longs that are keys
 * @param partitions A list of Integers that are the partitions
 * @param offsets    A list of Longs that are the offsets
 */
@Override
@KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}", topics = "${order-service" +
".payment-response-topic-name}")
public void receive(@Payload List<PaymentResponseAvroModel> messages,
                    @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                    @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                    @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    log.info("{} number of payment responses received with keys:{}, partitions:{} and offsets:{}",
    messages.size(),
    keys.toString(),
    partitions.toString(),
    offsets.toString());

    messages.forEach(paymentResponseAvroModel -> {
        if( paymentResponseAvroModel.getPaymentStatus().name().equals("COMPLETED") ) {
            log.info("Processing successful payment for order id: {}", paymentResponseAvroModel.getOrderId());
            paymentResponseMessageListener.paymentCompleted(orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(
            paymentResponseAvroModel));
        } else if( paymentResponseAvroModel.getPaymentStatus().name().equals("CANCELLED") || paymentResponseAvroModel
        .getPaymentStatus()
        .name()
        .equals("FAILED") ) {
            log.info("Processing unsuccessful payment for orderid: {}", paymentResponseAvroModel.getOrderId());
            paymentResponseMessageListener.paymentCancelled(orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(
            paymentResponseAvroModel));
        }
    });
}


}