package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {

private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;
private final OrderMessagingDataMapper orderMessagingDataMapper;

public RestaurantApprovalResponseKafkaListener(RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener,
                                               OrderMessagingDataMapper orderMessagingDataMapper) {
    this.restaurantApprovalResponseMessageListener = restaurantApprovalResponseMessageListener;
    this.orderMessagingDataMapper = orderMessagingDataMapper;
}


/**
 * Receive a kafka AvroModel and dependent on its status create an APPROVED or REJECTED ApprovalResponse using
 * OrderMessagingDataMapper
 *
 * @param messages   A list of given Type of messages
 * @param keys       A list of Longs that are keys
 * @param partitions A list of Integers that are the partitions
 * @param offsets    A list of Longs that are the offsets
 */
@Override
@KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}", topics = "${order-service" +
".restaurant-approval-response-topic-name}")
public void receive(@Payload List<RestaurantApprovalResponseAvroModel> messages,
                    @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                    @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                    @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
    log.info("{} number of restaurant approval responses received with keys {}, partitions {} and offsets {}",
    messages.size(),
    keys.toString(),
    partitions.toString(),
    offsets.toString());

    messages.forEach(restaurantApprovalResponseAvroModel -> {
        try {
            if( restaurantApprovalResponseAvroModel.getOrderApprovalStatus().name().equals("APPROVED") ) {
                log.info("Processing approved order for order id: {}", restaurantApprovalResponseAvroModel.getOrderId());
                restaurantApprovalResponseMessageListener.orderApproved(orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(
                restaurantApprovalResponseAvroModel));
            } else if( restaurantApprovalResponseAvroModel.getOrderApprovalStatus().name().equals("REJECTED") ) {
                log.info("Processing rejected order for order id: {}, with failure messages: {}",
                restaurantApprovalResponseAvroModel.getOrderId(),
                restaurantApprovalResponseAvroModel.getFailureMessages());
                restaurantApprovalResponseMessageListener.orderRejected(orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(
                restaurantApprovalResponseAvroModel));
            }
          } catch (OptimisticLockingFailureException e) {
            // 1. NO-OP for OptimisticLockingFailureException This means another thread finished the work, do not
            //throw error to prevent reading data from kafka again.
            log.error("Caught optimistic locking exception in RestaurantApprovalResponseKafkaListener for order id: {}",
                    restaurantApprovalResponseAvroModel.getOrderId());
        } catch (OrderNotFoundException e) {
            // 2.  NO - Operation for OrderNotFoundException, If OrderPaymentSaga.findOrder fails to find the order:
            log.error("No order found for order id: {}", restaurantApprovalResponseAvroModel.getOrderId());
        } // Any failure other than these == Spring retries
    });

}


}
