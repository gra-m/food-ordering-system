package com.food.ordering.system.restaurant.messaging.publisher.kafka;

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.restaurant.messaging.mapper.RestaurantMessagingDataMapper;
import com.food.ordering.system.restaurant.service.domain.config.RestaurantServiceConfigData;
import com.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import com.food.ordering.system.restaurant.service.domain.ports.output.message.publiser.OrderRejectedMessagePublisher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OrderRejectedKafkaMessagePublisher implements OrderRejectedMessagePublisher {
private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;
private final KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer;
private final RestaurantServiceConfigData restaurantServiceConfigData;
private final KafkaMessageHelper kafkaMessageHelper;

public OrderRejectedKafkaMessagePublisher(RestaurantMessagingDataMapper restaurantMessagingDataMapper,
                                          KafkaProducer<String, RestaurantApprovalResponseAvroModel> kafkaProducer,
                                          RestaurantServiceConfigData restaurantServiceConfigData,
                                          KafkaMessageHelper kafkaMessageHelper) {
    this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
    this.kafkaProducer = kafkaProducer;
    this.restaurantServiceConfigData = restaurantServiceConfigData;
    this.kafkaMessageHelper = kafkaMessageHelper;
}


/**
 * @param orderRejectedEvent
 */
@Override
public void publish(OrderRejectedEvent orderRejectedEvent) {
    String orderId = orderRejectedEvent.getOrderApproval().getOrderId().getValue().toString();

    log.info("Received OrderRejectedEvent for order id: {}", orderId);

    try {
        RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel =
        restaurantMessagingDataMapper.orderRejectedEventToRestaurantApprovalResponseAvroModel(
        orderRejectedEvent);

        kafkaProducer.send(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
        orderId,
        restaurantApprovalResponseAvroModel,
        kafkaMessageHelper.getKafkaCallback(restaurantServiceConfigData.getRestaurantApprovalResponseTopicName(),
        restaurantApprovalResponseAvroModel,
        orderId,
        "RestaurantApprovalResponseAvroModel"));

        log.info("RestaurantApprovalAvroModel sent to Kafka at {}", System.nanoTime());

    }
    catch( Exception e ) {
        log.error("Error while sending RestaurantApprovalAvroModel message to Kafka with order id: {} error: {}",
        orderId,
        e.getMessage());

    }
}


}
