package com.food.ordering.system.restaurant.messaging.mapper;

import com.food.ordering.system.domain.valueobject.ProductId;
import com.food.ordering.system.domain.valueobject.RestaurantOrderStatus;
import com.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest;
import com.food.ordering.system.restaurant.service.domain.entity.Product;
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovedEvent;
import com.food.ordering.system.restaurant.service.domain.event.OrderRejectedEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Slf4j
@Component
public class RestaurantMessagingDataMapper {
private final Logger LOG = LoggerFactory.getLogger(RestaurantMessagingDataMapper.class);

public RestaurantApprovalResponseAvroModel orderApprovedEventToRestaurantApprovalResponseAvroModel(OrderApprovedEvent orderApprovedEvent) {
    return RestaurantApprovalResponseAvroModel
    .newBuilder()
    .setId(UUID.randomUUID().toString())
    .setSagaId("")
    .setOrderId(orderApprovedEvent.getOrderApproval().getOrderId().getValue().toString())
    .setRestaurantId(orderApprovedEvent.getRestaurantId().getValue().toString())
    .setCreatedAt(orderApprovedEvent.getCreatedAt().toInstant())
    .setOrderApprovalStatus(OrderApprovalStatus.valueOf(orderApprovedEvent
    .getOrderApproval()
    .getApprovalStatus()
    .name()))
    .setFailureMessages(orderApprovedEvent.getFailureMessages())
    .build();
}

public RestaurantApprovalResponseAvroModel orderRejectedEventToRestaurantApprovalResponseAvroModel(OrderRejectedEvent orderRejectedEvent) {
    return RestaurantApprovalResponseAvroModel
    .newBuilder()
    .setId(UUID.randomUUID().toString())
    .setSagaId("")
    .setOrderId(orderRejectedEvent.getOrderApproval().getOrderId().getValue().toString())
    .setRestaurantId(orderRejectedEvent.getRestaurantId().getValue().toString())
    .setCreatedAt(orderRejectedEvent.getCreatedAt().toInstant())
    .setOrderApprovalStatus(OrderApprovalStatus.valueOf(orderRejectedEvent
    .getOrderApproval()
    .getApprovalStatus()
    .name()))
    .setFailureMessages(orderRejectedEvent.getFailureMessages())
    .build();
}

//todo remove testing code
public RestaurantApprovalRequest restaurantApprovalRequestAvroModelToRestaurantApproval(
RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel) {


    LOG.info("A: Converting before approval, AvroModels product Ids are: {}",
    restaurantApprovalRequestAvroModel.getProducts().stream().map(com.food.ordering.system.kafka.order.avro.model.Product::getId).toList());


    RestaurantApprovalRequest approvalRequest =
    RestaurantApprovalRequest
    .builder()
    .id(restaurantApprovalRequestAvroModel.getId())
    .sagaId(restaurantApprovalRequestAvroModel.getSagaId())
    .restaurantId(restaurantApprovalRequestAvroModel.getRestaurantId())
    .orderId(restaurantApprovalRequestAvroModel.getOrderId())
    .restaurantOrderStatus(RestaurantOrderStatus.valueOf(restaurantApprovalRequestAvroModel
    .getRestaurantOrderStatus().name()))
    .products(restaurantApprovalRequestAvroModel
    .getProducts()
    .stream()
    .map(avroModel -> com.food.ordering.system.restaurant.service.domain.entity.Product
    .builder()
    .productId(new ProductId(UUID.fromString(avroModel.getId())))
    .quantity(avroModel.getQuantity())
    .build())
    .toList())
    .price(restaurantApprovalRequestAvroModel.getPrice())
    .createdAt(restaurantApprovalRequestAvroModel.getCreatedAt())
    .build();


    List<UUID> productIds= approvalRequest.getProducts().stream().map(product -> product.getId().getValue()).toList();
    LOG.info("B: After Avro Approval Request converted Approval request product ids are: {}", productIds);

    return approvalRequest;
}



}
