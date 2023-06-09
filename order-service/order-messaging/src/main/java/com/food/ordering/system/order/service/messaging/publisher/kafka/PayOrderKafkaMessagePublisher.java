package com.food.ordering.system.order.service.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.order.service.domain.config.OrderServiceConfigData;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.OrderPaidRestaurantRequestMessagePublisher;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Messages published from here will be handled by kafka topic RestaurantApprovalRequest, which will expect a
 * RestaurantApprovalRequestAvroModel
 */
@Slf4j
@Component
public class PayOrderKafkaMessagePublisher implements OrderPaidRestaurantRequestMessagePublisher {

private final OrderMessagingDataMapper orderMessagingDataMapper;
private final OrderServiceConfigData orderServiceConfigData;
private final KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer;

public PayOrderKafkaMessagePublisher(OrderMessagingDataMapper orderMessagingDataMapper,
                                     OrderServiceConfigData orderServiceConfigData,
                                     KafkaProducer<String, RestaurantApprovalRequestAvroModel> kafkaProducer) {
      this.orderMessagingDataMapper = orderMessagingDataMapper;
      this.orderServiceConfigData = orderServiceConfigData;
      this.kafkaProducer = kafkaProducer;
}


/**
 * @param domainEvent
 */
@Override
public void publish(OrderPaidEvent domainEvent) {
      String orderId = domainEvent.getOrder().getId().getValue().toString();

      RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
          orderMessagingDataMapper.orderPaidEventToRestaurantApprovalRequestAvroModel(domainEvent);

      kafkaProducer.send(orderServiceConfigData.getRestaurantApprovalRequestTopicName(),
          orderId,
          restaurantApprovalRequestAvroModel,)

}
}
