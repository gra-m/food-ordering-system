package com.food.ordering.system.order.service.dataaccess.order.mapper;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.food.ordering.system.order.service.domain.entity.Order.FAILURE_MESSAGE_DELIMITER;


/**
 * EntityObjects -> DomainObjects -> EntityObjects..
 */
@Component
public class OrderDataAccessMapper {


/**
 * Transforms Order into OrderEntity using a couple of private helper methods for the nested objects.
 *
 * @see #deliveryAddressToAddressEntity(StreetAddress)
 * @see #orderItemsToOrderItemEntities(List)
 *
 * @param order Order
 * @return OrderEntity
 */
public OrderEntity orderToOrderEntity(Order order) {

      OrderEntity orderEntity = OrderEntity
          .builder()
          .id(order.getId().getValue())
          .customerId(order.getCustomerId().getValue())
          .restaurantId(order.getRestaurantId().getValue())
          .trackingId(order.getTrackingId().getValue())
          .address(deliveryAddressToAddressEntity(order.getDeliveryAddress()))
          .price(order.getPrice().getAmount())
          .items(orderItemsToOrderItemEntities(order.getItems()))
          .orderStatus(order.getOrderStatus())
          .failureMessages(order.getFailureMessages() != null ? String.join(FAILURE_MESSAGE_DELIMITER,
              order.getFailureMessages()) : "")
          .build();

      orderEntity.getAddress().setOrder(orderEntity);
      orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));

      return orderEntity;
}


/**
 * Transforms OrderEntity into Order domain object using a couple of private helper methods for the nested objects.
 *
 * Note, in order to make the item list modifiable in the business logic Arrays.asList is wrapped in a new ArrayList
 * constructor. Arrays.asList returns List.
 *
 * @see #addressEntityToDeliveryAddress(OrderAddressEntity)
 * @see #orderItemEntitiesToOrderItems(List)
 *
 * @param orderEntity
 * @return Order
 */
public Order orderEntityToOrder(OrderEntity orderEntity) {

      return Order
          .builder()
          .orderId(new OrderId(orderEntity.getId()))
          .customerId(new CustomerId(orderEntity.getCustomerId()))
          .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
          .trackingId(new TrackingId(orderEntity.getTrackingId()))
          .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.getAddress()))
          .price(new Money(orderEntity.getPrice()))
          .items(orderItemEntitiesToOrderItems(orderEntity.getItems()))
          .orderStatus(orderEntity.getOrderStatus())
          .failureMessages(orderEntity.getFailureMessages().isEmpty() ? new ArrayList<>():
              new ArrayList<>(Arrays.asList(orderEntity.getFailureMessages().split(FAILURE_MESSAGE_DELIMITER))))
          .build();
}


private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> items) {

      return items
          .stream()
          .map(oie -> OrderItem
              .builder()
              .orderItemId(new OrderItemId(oie.getId()))
              .price(new Money(oie.getPrice()))
              .product(new Product(new ProductId(oie.getProductId())))
              .quantity(oie.getQuantity())
              .subTotal(new Money(oie.getPrice()))
              .build())
              .collect(Collectors.toList());
}

private StreetAddress addressEntityToDeliveryAddress(OrderAddressEntity address) {
      return new StreetAddress(address.getId(), address.getStreet(), address.getPostalCode(), address.getCity());
}




private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items) {
      /*List<OrderItemEntity> returnArrayList = new ArrayList<>(items.size());
      for( OrderItem item : items ) {
            returnArrayList.add(OrderItemEntity
                .builder()
                .id(item.getId().getValue())
                .productId(item.getProduct().getId().getValue())
                .quantity((item.getQuantity()))
                .price(item.getPrice().getAmount())
                .subTotal(item.getSubTotal().getAmount())
                .build());
      }*/
      return items
          .stream()
          .map(orderItem -> OrderItemEntity
              .builder()
              .id(orderItem.getId().getValue())
              .productId(orderItem.getProduct().getId().getValue())
              .quantity((orderItem.getQuantity()))
              .price(orderItem.getPrice().getAmount())
              .subTotal(orderItem.getSubTotal().getAmount())
              .build())
          .collect(Collectors.toList());
}

private OrderAddressEntity deliveryAddressToAddressEntity(StreetAddress deliveryAddress) {

      return OrderAddressEntity
          .builder()
          .id(deliveryAddress.getId())
          .street(deliveryAddress.getStreet())
          .city(deliveryAddress.getCity())
          .postalCode(deliveryAddress.getPostalCode())
          .build();
}
}
