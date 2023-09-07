package com.food.ordering.system.payment.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.outbox.OutboxStatus;
import com.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload;
import com.food.ordering.system.payment.service.domain.outbox.model.OrderOutboxMessage;
import com.food.ordering.system.payment.service.domain.ports.output.message.publisher.PaymentResponseMessagePublisher;
import com.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.function.BiConsumer;

@Component
@Slf4j
public class PaymentEventKafkaPublisher implements PaymentResponseMessagePublisher
{
    private final PaymentMessagingDataMapper paymentMessagingDataMapper;
    private final KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer;
    private final PaymentServiceConfigData paymentServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    public PaymentEventKafkaPublisher(PaymentMessagingDataMapper paymentMessagingDataMapper,
                                      KafkaProducer<String, PaymentResponseAvroModel> kafkaProducer,
                                      PaymentServiceConfigData paymentServiceConfigData,
                                      KafkaMessageHelper kafkaMessageHelper)
    {
        this.paymentMessagingDataMapper = paymentMessagingDataMapper;
        this.kafkaProducer = kafkaProducer;
        this.paymentServiceConfigData = paymentServiceConfigData;
        this.kafkaMessageHelper = kafkaMessageHelper;
    }


    @Override
    public void publish(OrderOutboxMessage orderOutboxMessage,
                        BiConsumer<OrderOutboxMessage, OutboxStatus> outboxCallback)
    {
        // Retrieve json payload of outputType OrderEventPayload.class
        OrderEventPayload orderEventPayload =
                kafkaMessageHelper.getOrderEventPayload(orderOutboxMessage.getPayload(), OrderEventPayload.class);

        String sagaId = orderOutboxMessage.getSagaId().toString();

        log.info("Received OrderOutboxMessage for order id: {} and saga id {}",
                orderEventPayload.getOrderId(),
                sagaId);

        try {
            // create  payment response from sagaId and the orderEventPayload
            PaymentResponseAvroModel paymentResponseAvroModel = paymentMessagingDataMapper
                    .orderEvenPayloadToPaymentResponseAvroModel(sagaId, orderEventPayload);

            // create callback
            ListenableFutureCallback<SendResult<String, PaymentResponseAvroModel>> callback = kafkaMessageHelper.getKafkaCallback(
                    paymentServiceConfigData.getPaymentResponseTopicName(),
                    paymentResponseAvroModel,
                    orderOutboxMessage,
                    outboxCallback,
                    orderEventPayload.getOrderId(),
                    "PaymentResponseAvroModel"
            );

            // send avro model with topicname sagaId avro model and callback. The callback is called after getting ack
            // from kafka
            kafkaProducer.send(
                    paymentServiceConfigData.getPaymentRequestTopicName(),
                    sagaId,
                    paymentResponseAvroModel,
                    callback
            );
            log.info("PaymentResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                    paymentResponseAvroModel.getOrderId(),
                    sagaId);
        } catch (Exception e) {
            log.error("Error while sending PaymentRequestAvroModel message to kafka with order id: {} saga id: {}" +
                    "and error:\n{} ",
                    orderEventPayload.getOrderId(),
                    sagaId,
                    e.getMessage());
        }


    }
}
