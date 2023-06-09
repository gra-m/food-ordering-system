package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Slf4j
@Component
public class OrderKafkaMessageHelper {

/**
 * Async callback will at present just confirm that publishing failed with a log error or was successful, in which
 * case the metadata from the SendResult is logged.
 * @param paymentResponseTopicName retrieved from orderServiceConfigData
 * @param paymentRequestAvroModel  created in the publish method
 * @return a ListenableFutureCallback, currently just logging [fixme]
 */
public ListenableFutureCallback<SendResult<String, PaymentRequestAvroModel>> getKafkaCallback(String paymentResponseTopicName,
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
