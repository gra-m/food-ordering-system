package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
private final OrderMessagingDataMapper orderMessagingDataMapper;

public RestaurantApprovalResponseKafkaListener(
RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener,
OrderMessagingDataMapper orderMessagingDataMapper) {
    this.restaurantApprovalResponseMessageListener = restaurantApprovalResponseMessageListener;
    this.orderMessagingDataMapper = orderMessagingDataMapper;
}


/**
 * Receive a kafka AvroModel and dependent on its status create an APPROVED or REJECTED ApprovalResponse using
 * OrderMessagingDataMapper
 *
 * fixme RestaurantApprovalResponseMessageListenerImpl will be implemented during SAGA refactoring.
 *
 * @param messages   A list of given Type of messages
 * @param keys       A list of Longs that are keys
 * @param partitions A list of Integers that are the partitions
 * @param offsets    A list of Longs that are the offsets
 */
@Override
public void receive(@Payload List<RestaurantApprovalResponseAvroModel> messages,
                    @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Long> keys,
                    @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                    @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    log.info("{} number of restaurant approval responses received with keys {}, partitions {} and offsets {}",
    messages.size(),
    keys.toString(),
    partitions.toString(),
    offsets.toString());

    messages.forEach(restaurantApprovalResponseAvroModel -> {
        if( restaurantApprovalResponseAvroModel.getOrderApprovalStatus().name().equals("APPROVED") ) {
            log.info("Processing approved order for order id: {}", restaurantApprovalResponseAvroModel.getOrderId());
            restaurantApprovalResponseMessageListener.orderApproved(orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(
            restaurantApprovalResponseAvroModel));
        } else if( restaurantApprovalResponseAvroModel.getOrderApprovalStatus().name().equals("REJECTED") ) {
            log.info("Processing rejected order for order id: {}, with failure messages: {}",
            restaurantApprovalResponseAvroModel.getFailureMessages());
            restaurantApprovalResponseMessageListener.orderRejected(orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(
            restaurantApprovalResponseAvroModel));
        }
    });

}
}