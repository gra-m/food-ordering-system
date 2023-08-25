package com.food.ordering.system.order.service.domain.mapper;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand;
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse;
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Given that input Order Data Transfer Objects need to be mapped to domain objects and domain objects need to be
 * mapped to output objects.
 * <p>From a DDD perspective you could consider this the 'Factory' as creation of and conversion of Domain
 * objects and DTO's is delegated to this Mapper Class</p>
 * <a href="https://stackoverflow.com/questions/555241/domain-driven-design-and-the-role-of-the-factory-class">
 * more about factories in Domain Driven design, and links to books...</a>
 */

@Component
public class OrderDataMapper {


/**
 * @param order
 * @return
 */
public CreateOrderResponse orderToCreateOrderResponse(Order order, String message) {
    return CreateOrderResponse
    .builder()
    .orderTrackingId(order.getTrackingId().getValue())
    .orderStatus(order.getOrderStatus())
    .message(message)
    .build();
}

/**
 * @param order
 * @return
 */
public TrackOrderResponse orderToTrackOrderResponse(Order order) {
    return TrackOrderResponse
    .builder()
    .orderTrackingId(order.getTrackingId().getValue())
    .orderStatus(order.getOrderStatus())
    .failureMessages(order.getFailureMessages())
    .build();

}

public OrderPaymentEventPayload orderCreatedEventToOrderPaymentEventPayload(OrderCreatedEvent orderCreatedEvent) {
    return OrderPaymentEventPayload.builder()
            .customerId(orderCreatedEvent.getOrder().getCustomerId().getValue().toString())
            .orderId(orderCreatedEvent.getOrder().getId().getValue().toString())
            .price(orderCreatedEvent.getOrder().getPrice().getAmount())
            .createdAt(orderCreatedEvent.getCreatedAt())
            .paymentOrderStatus(PaymentOrderStatus.PENDING.name())
            .build();
}

/**
 * CreateOrderCommand Data Transfer Object is used to create a Restaurant Domain object.
 *
 * @param createOrderCommand The DTO that is to be transposed into a Restaurant Object.
 * @return
 */
public Restaurant createOrderCommandToRestaurant(CreateOrderCommand createOrderCommand) {
    return Restaurant
    .builder()
    .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
    .products(createOrderCommand
    .getItems()
    .stream()
    .map(orderItem -> new Product(new ProductId(orderItem.getProductId())))
    .collect(Collectors.toList()))
    .build();
}

public Order createOrderCommandToOrder(CreateOrderCommand createOrderCommand) {
    return Order
    .builder()
    .customerId(new CustomerId(createOrderCommand.getCustomerId()))
    .restaurantId(new RestaurantId(createOrderCommand.getRestaurantId()))
    .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.getAddress()))
    .price(new Money(createOrderCommand.getPrice()))
    .items(orderItemsToOrderItemEntities(createOrderCommand.getItems()))
    .build();

}

private List<OrderItem> orderItemsToOrderItemEntities(List<com.food.ordering.system.order.service.domain.dto.create.OrderItem> orderItems) {
    return orderItems
    .stream()
    .map(orderItem -> OrderItem
    .builder()
    .product(new Product(new ProductId(orderItem.getProductId())))
    .price(new Money(orderItem.getPrice()))
    .quantity(orderItem.getQuantity())
    .subTotal(new Money(orderItem.getSubTotal()))
    .build())
    .collect(Collectors.toList());
}

private StreetAddress orderAddressToStreetAddress(OrderAddress orderAddress) {
    return new StreetAddress(UUID.randomUUID(),
    orderAddress.getStreet(),
    orderAddress.getPostalCode(),
    orderAddress.getCity());
}


}
