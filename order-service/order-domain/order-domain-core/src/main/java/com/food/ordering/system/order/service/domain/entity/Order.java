package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.exception.DomainException;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;
// fixme probably going to have version issues with compile as the aggregate [M] have do not have their children
//  listed as dependencies


/**
 * No setters though not immutable -> state changes will be through well named methods.
 * Note -> issues with OrderID as parameter was not added to AggregateRoot
 */
public class Order extends AggregateRoot<OrderId> {
private final CustomerId customerId;
private final RestaurantId restaurantId;
private final StreetAddress deliveryAddress;
private final Money price;
private final List<OrderItem> items;

private TrackingId trackingId;
private OrderStatus orderStatus;
private List<String> failureMessages;

private Order(Builder builder) {
      super.setId(builder.orderId);
      customerId = builder.customerId;
      restaurantId = builder.restaurantId;
      deliveryAddress = builder.deliveryAddress;
      price = builder.price;
      items = builder.items;
      trackingId = builder.trackingId;
      orderStatus = builder.orderStatus;
      failureMessages = builder.failureMessages;
}

public void validateOrder() {
      validateInitialOrder();
      validateTotalPrice();
      validateItemsPrice();
}

/**
 * Example -> creating constant in a class with accumulator methods for use with reduce.
 */
private void validateItemsPrice() {

      Money orderItemsTotal = items.stream().map(orderItem -> {
            validateItem_Price(orderItem);
            return orderItem.getSubTotal();
      }).reduce(Money.ZERO, Money::add);

      if( !price.equals(orderItemsTotal) )
            throw new OrderDomainException(String.format("Total price: %s is not equal to Order Items Total: %s !",
                price.getAmount(), orderItemsTotal.getAmount()));


}


/**
 *
 * Check delegated to OrderItem entity with
 *
 * @param orderItem
 */
private void validateItem_Price(OrderItem orderItem) {
       if(!orderItem.isPriceValid()) {
             throw new DomainException(String.format("Order item price: %s is not valid for product: %s",
                 orderItem.getPrice().getAmount(), orderItem.getProduct().getId().getValue()));
       }
}

/**
 * Checking price Money object has been initialized to an amount greater than zero.
 */
private void validateTotalPrice() {
      if( price == null && !price.isGreaterThanZero() ) {
            throw new OrderDomainException("Total price must be greater than zero!");

      }
}

/**
 * Checking order does not exist already order status and id should be null.
 */
private void validateInitialOrder() {
      if( orderStatus != null && getId() != null ) {
            throw new OrderDomainException("Order is not in correct state for initialization!");
      }
}

/**
 * todo take a look at his BaseEntity class, his == no setId highlight and no need to cast super.getId to (OrderId)
 */
public void initializeOrder() {
      setId(new OrderId(UUID.randomUUID()));
      trackingId = new TrackingId(UUID.randomUUID());
      orderStatus = OrderStatus.PENDING;
      initializeOrderItems();


}

private void initializeOrderItems() {
      long itemId = 1L;

      for( OrderItem orderItem : items ) {
            orderItem.initializeOrderItem(( OrderId ) super.getId(), new OrderItemId(itemId++));
      }

}


public CustomerId getCustomerId() {
      return customerId;
}

public RestaurantId getRestaurantId() {
      return restaurantId;
}

public StreetAddress getDeliveryAddress() {
      return deliveryAddress;
}

public Money getPrice() {
      return price;
}

public List<OrderItem> getItems() {
      return items;
}

public TrackingId getTrackingId() {
      return trackingId;
}

public OrderStatus getOrderStatus() {
      return orderStatus;
}

public List<String> getFailureMessages() {
      return failureMessages;
}

public static final class Builder {
      private OrderId orderId;
      private CustomerId customerId;
      private RestaurantId restaurantId;
      private StreetAddress deliveryAddress;
      private Money price;
      private List<OrderItem> items;
      private TrackingId trackingId;
      private OrderStatus orderStatus;
      private List<String> failureMessages;

      private Builder() {
      }

      public static Builder builder() {
            return new Builder();
      }

      public Builder orderId(OrderId val) {
            orderId = val;
            return this;
      }

      public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
      }

      public Builder restaurantId(RestaurantId val) {
            restaurantId = val;
            return this;
      }

      public Builder deliveryAddress(StreetAddress val) {
            deliveryAddress = val;
            return this;
      }

      public Builder price(Money val) {
            price = val;
            return this;
      }

      public Builder items(List<OrderItem> val) {
            items = val;
            return this;
      }

      public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
      }

      public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
      }

      public Builder failureMessages(List<String> val) {
            failureMessages = val;
            return this;
      }

      public Order build() {
            return new Order(this);
      }
}
}
