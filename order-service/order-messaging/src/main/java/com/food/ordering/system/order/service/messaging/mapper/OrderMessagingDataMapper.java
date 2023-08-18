package com.food.ordering.system.order.service.messaging.mapper;

import com.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.system.domain.valueobject.PaymentStatus;
import com.food.ordering.system.kafka.order.avro.model.*;
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse;
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
<<<<<<< Updated upstream

/**
 * fixme
 * Mapping received order events to PaymentRequestAvroModel objects for Kafka ..
 * Or mapping a model to another required object.
 */
=======
@Slf4j
>>>>>>> Stashed changes
@Component
public class OrderMessagingDataMapper {
private final Logger LOG = LoggerFactory.getLogger(OrderMessagingDataMapper.class);

public RestaurantApprovalResponse approvalResponseAvroModelToApprovalResponse(
RestaurantApprovalResponseAvroModel restaurantApprovalResponseAvroModel) {

    return RestaurantApprovalResponse
    .builder()
    .id(restaurantApprovalResponseAvroModel.getId())
    .sagaId(restaurantApprovalResponseAvroModel.getSagaId())
    .restaurantId(restaurantApprovalResponseAvroModel.getRestaurantId())
    .orderId(restaurantApprovalResponseAvroModel.getOrderId())
    .createdAt(restaurantApprovalResponseAvroModel.getCreatedAt())
    .orderApprovalStatus(OrderApprovalStatus.valueOf(restaurantApprovalResponseAvroModel
    .getOrderApprovalStatus()
    .name()))
    .failureMessages(restaurantApprovalResponseAvroModel.getFailureMessages())
    .build();
}

public PaymentResponse paymentResponseAvroModelToPaymentResponse(PaymentResponseAvroModel paymentResponseAvroModel) {

    return PaymentResponse
    .builder()
    .id(paymentResponseAvroModel.getId())
    .sagaId(paymentResponseAvroModel.getSagaId())
    .paymentId(paymentResponseAvroModel.getPaymentId())
    .customerId(paymentResponseAvroModel.getCustomerId())
    .orderId(paymentResponseAvroModel.getOrderId())
    .price(paymentResponseAvroModel.getPrice())
    .createdAt(paymentResponseAvroModel.getCreatedAt())
    .paymentStatus(PaymentStatus.valueOf(paymentResponseAvroModel.getPaymentStatus().name()))
    .failureMessages(paymentResponseAvroModel.getFailureMessages())
    .build();
}


public RestaurantApprovalRequestAvroModel orderPaidEventToRestaurantApprovalRequestAvroModel(
OrderPaidEvent orderPaidEvent) {
    Order order = orderPaidEvent.getOrder();

    RestaurantApprovalRequestAvroModel restaurantApprovalRequestAvroModel =
    RestaurantApprovalRequestAvroModel.newBuilder()
    .setId(UUID.randomUUID().toString())
    .setSagaId("")
    .setOrderId(order.getId().getValue().toString())
    .setRestaurantId(order.getRestaurantId().getValue().toString())
    .setRestaurantOrderStatus(RestaurantOrderStatus.valueOf(order.getOrderStatus().name()))
    .setProducts(createProductListFromItems(order))
    .setPrice(order.getPrice().getAmount())
    .setCreatedAt(orderPaidEvent.getCreatedAt().toInstant())
    .setRestaurantOrderStatus(RestaurantOrderStatus.PAID)
    .build();

    List<Product> retrievedProducts = restaurantApprovalRequestAvroModel.getProducts();

    List<String> ids = retrievedProducts.stream().map(product -> product.getId()).toList();

    //todo remove test log/code
    LOG.info("XX XXXXXXXXXXXXXXXXXXXXXXXXXXXX The ids of ordered products are {}", ids);
    return restaurantApprovalRequestAvroModel;

}

private List<Product> createProductListFromItems(Order order) {
    return order
    .getItems()
    .stream()
    .map(item -> Product
    .newBuilder()
    .setId(item.getProduct().getId().getValue().toString())
    .setQuantity(item.getQuantity())
    .build())
    .collect(Collectors.toList());

}


/**
 * Retrieve order, set new avro fields and set retrieved order fields, payment status set to PENDING.
 * <p>New into avroObject ==  set random Id, set Saga id payment [todo implement SAGA] status PENDING</p>
 * <p>The rest of the fields are from retrieved Order</p>
 *
 * @param orderCreatedEvent containing order details
 * @return PaymentRequestAvroModel for Kafka with PENDING status
 */
public PaymentRequestAvroModel orderCreatedEventToPaymentRequestAvroModel(OrderCreatedEvent orderCreatedEvent) {

    Order order = orderCreatedEvent.getOrder();

    return PaymentRequestAvroModel
    .newBuilder()
    .setId(UUID.randomUUID().toString())
    .setSagaId("")
    .setCustomerId(order.getCustomerId().getValue().toString())
    .setOrderId(order.getId().getValue().toString())
    .setPrice(order.getPrice().getAmount())
    .setCreatedAt(orderCreatedEvent.getCreatedAt().toInstant())
    .setPaymentOrderStatus(PaymentOrderStatus.PENDING)
    .build();

}


/**
 * Retrieve order, set new avro fields and set retrieved order fields, payment status set to CANCELLED.
 * <p>New into avroObject ==  set random Id, set Saga id payment [to be implemented] status CANCELLED</p>
 * <p>The rest of the fields are from retrieved Order</p>
 *
 * @param orderCancelledEvent containing order details
 * @return PaymentRequestAvroModel for Kafka with CANCELLED status
 */
public PaymentRequestAvroModel orderCancelledEventToPaymentRequestAvroModel(OrderCancelledEvent orderCancelledEvent) {

    Order order = orderCancelledEvent.getOrder();

    return PaymentRequestAvroModel
    .newBuilder()
    .setId(UUID.randomUUID().toString())
    .setSagaId("")
    .setCustomerId(order.getCustomerId().getValue().toString())
    .setOrderId(order.getId().getValue().toString())
    .setPrice(order.getPrice().getAmount())
    .setCreatedAt(orderCancelledEvent.getCreatedAt().toInstant())
    .setPaymentOrderStatus(PaymentOrderStatus.CANCELLED)
    .build();

}

}
