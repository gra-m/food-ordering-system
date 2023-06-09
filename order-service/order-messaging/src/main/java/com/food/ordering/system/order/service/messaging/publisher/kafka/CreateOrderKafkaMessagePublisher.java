package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCreatedPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class CreateOrderKafkaMessagePublisher implements OrderCreatedPaymentRequestMessagePublisher {

private final OrderMessagingDataMapper orderMessagingDataMapper;
private final OrderServiceConfigData orderServiceConfigData;
private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;

public CreateOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                        OrderServiceConfigData orderServiceConfigData,
                                        KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer) {
      this.orderMessagingDataMapper = orderMessagingDataMapper;
      this.orderServiceConfigData = orderServiceConfigData;
      this.kafkaProducer = kafkaProducer;
}

/**
 * Given that an OrderCreatedEvent has been received this needs to be published to Kafka for propagation/checking
 * [fixme]
 * 1. The order id is extracted from the domainEvent
 * 2. A PaymentRequestAvroModel is created
 * 3. A message is sent to the KafkaProducer specified in he orderServiceConfigData along with a callback method created
 * @see #getKafkaCallback(String, PaymentRequestAvroModel)
 *
 * @param domainEvent  an order has been created
 */
@Override
public void publish(OrderCreatedEvent domainEvent) {
      String orderId = domainEvent.getOrder().getId().getValue().toString();
      log.info("Received OrderCreatedEvent for order id: {}", orderId);

      try {
            PaymentRequestAvroModel paymentRequestAvroModel =
                orderMessagingDataMapper.orderCreatedEventToPaymentRequestAvroModel(
                domainEvent);

            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                orderId,
                paymentRequestAvroModel,
                getKafkaCallback(orderServiceConfigData.getPaymentRequestTopicName(), paymentRequestAvroModel));

            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", paymentRequestAvroModel.getOrderId());
      }
      catch( Exception e ) {
            log.error("Error while sending PaymentRequestAvroModel message to kafka with order id: {}, error: {}",
                orderId, e.getMessage());
      }
}

/**
 * Async callback will at present just confirm that publishing failed with a log error or was successful, in which
 * case the metadata from the SendResult is logged.
 * @param paymentResponseTopicName retrieved from orderServiceConfigData
 * @param paymentRequestAvroModel  created in the publish method
 * @return a ListenableFutureCallback, currently just logging [fixme]
 */
private ListenableFutureCallback<SendResult<String, PaymentRequestAvroModel>> getKafkaCallback(String paymentResponseTopicName,
                                                                                               PaymentRequestAvroModel paymentRequestAvroModel) {

      return new ListenableFutureCallback<SendResult<String, PaymentRequestAvroModel>>() {
            @Override
            public void onFailure(Throwable ex) {
                  log.error("Error while sending PaymentRequestAvroModel message {} to topic {}",
                      paymentRequestAvroModel.toString(),
                      paymentResponseTopicName,
                      ex);
            }

            @Override
            public void onSuccess(SendResult<String, PaymentRequestAvroModel> result) {
                  RecordMetadata metadata = result.getRecordMetadata();
                  log.info("Received successful response from Kafka for order id: {}" + " Topic: {} Partition {} " +
                          "Offset: {} Timestamp: {}",
                      paymentRequestAvroModel.getOrderId(),
                      metadata.topic(),
                      metadata.partition(),
                      metadata.offset(),
                      metadata.timestamp());

            }
      };
}


}
