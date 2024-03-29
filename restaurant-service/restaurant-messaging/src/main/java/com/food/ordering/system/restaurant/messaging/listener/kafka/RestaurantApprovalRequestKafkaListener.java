package com.food.ordering.system.restaurant.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.restaurant.messaging.mapper.RestaurantMessagingDataMapper;
import com.food.ordering.system.restaurant.service.domain.ports.input.message.listener.RestaurantApprovalRequestMessageListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RestaurantApprovalRequestKafkaListener implements KafkaConsumer<RestaurantApprovalRequestAvroModel> {
private final RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener;
private final RestaurantMessagingDataMapper restaurantMessagingDataMapper;

public RestaurantApprovalRequestKafkaListener(RestaurantApprovalRequestMessageListener restaurantApprovalRequestMessageListener,
                                              RestaurantMessagingDataMapper restaurantMessagingDataMapper) {
    this.restaurantApprovalRequestMessageListener = restaurantApprovalRequestMessageListener;
    this.restaurantMessagingDataMapper = restaurantMessagingDataMapper;
}


/**
 * Given a batch of RestaurantApprovalRequestAvroModels have been received by the kafka consumer group/topic of this
 *
 * @param messages   A list of given Type of messages
 * @param keys       A list of Longs that are keys
 * @param partitions A list of Integers that are the partitions
 * @param offsets    A list of Longs that are the offsets
 * @KafkaListener convert them to RestaurantApproval objects and pass them to the
 * restaurantApprovalRequestMessageListener for persistence and event firing.
 * <p>
 * note: id/topics configured in microservices container application.yml
 */
@Override
@KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}", topics = "${restaurant-service" +
".restaurant-approval-request-topic-name}")
public void receive(@Payload List<RestaurantApprovalRequestAvroModel> messages,
                    @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                    @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                    @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    log.info("{} number of order approval requests received with keys {}, partitions {} and offsets {}," + "sending " +
    "for restaurant approval",
    messages.size(),
    keys.toString(),
    partitions.toString(),
    offsets.toString());

    messages.forEach(restaurantApprovalRequestAvroModel -> {
        log.info("Processing order approval for order id: {}", restaurantApprovalRequestAvroModel.getOrderId());
        restaurantApprovalRequestMessageListener.approveOrder(restaurantMessagingDataMapper.restaurantApprovalRequestAvroModelToRestaurantApproval(
        restaurantApprovalRequestAvroModel));
    });

}


}
