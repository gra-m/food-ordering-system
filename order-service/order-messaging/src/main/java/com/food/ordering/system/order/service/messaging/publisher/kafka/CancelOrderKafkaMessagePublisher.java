package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.OrderCancelledPaymentRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CancelOrderKafkaMessagePublisher implements OrderCancelledPaymentRequestMessagePublisher {

private final OrderMessagingDataMapper orderMessagingDataMapper;
private final OrderServiceConfigData orderServiceConfigData;
private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;
private final OrderKafkaMessageHelper orderKafkaMessageHelper;

public CancelOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                        OrderServiceConfigData orderServiceConfigData,
                                        KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer,
                                        OrderKafkaMessageHelper orderKafkaMessageHelper) {
      this.orderMessagingDataMapper = orderMessagingDataMapper;
      this.orderServiceConfigData = orderServiceConfigData;
      this.kafkaProducer = kafkaProducer;
      this.orderKafkaMessageHelper = orderKafkaMessageHelper;
}


/**
 * Given that an OrderCancelledEvent has been received this needs to be published to Kafka for propagation/checking
 * [fixme]
 * 1. The order id is extracted from the domainEvent
 * 2. A PaymentRequestAvroModel is created
 * 3. A message is sent to the KafkaProducer specified in the orderServiceConfigData along with a callback method
 * created in the HelperClass
 *
 * @param domainEvent an order has been created
 */
@Override
public void publish(OrderCancelledEvent domainEvent) {
      String orderId = domainEvent.getOrder().getId().getValue().toString();
      log.info("Received OrderCancelledEvent for order id: {}", orderId);

      try {
            PaymentRequestAvroModel paymentRequestAvroModel =
                orderMessagingDataMapper.orderCancelledEventToPaymentRequestAvroModel(
                domainEvent);

            kafkaProducer.send(orderServiceConfigData.getPaymentRequestTopicName(),
                orderId,
                paymentRequestAvroModel,
                orderKafkaMessageHelper.getKafkaCallback(orderServiceConfigData.getPaymentRequestTopicName(),
                    paymentRequestAvroModel));

            log.info("PaymentRequestAvroModel sent to Kafka for order id: {}", paymentRequestAvroModel.getOrderId());
      }
      catch( Exception e ) {
            log.error("Error while sending PaymentRequestAvroModel message to kafka with order id: {}, error: {}",
                orderId,
                e.getMessage());
      }
}


}
