package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;

/**
 * Order Item uniqueness is only important within context of order, so it does not need UUID, just
 * a number starting at 1.
 */
public class OrderItem extends BaseEntity<OrderItemId> {
      private OrderId orderId;
      private final Product product;
      private final int quantity;
      private final Money price;
      private final Money subTotal;

/**
 * Only called from Order during orderInitialization, so package private.
 */
void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
      super.setId(orderItemId);
      this.orderId = orderId;

}

private OrderItem(Builder builder) {
      super.setId(builder.orderItemId);
      product = builder.product;
      quantity = builder.quantity;
      price = builder.price;
      subTotal = builder.subTotal;
}


public OrderId getOrderId() {
      return orderId;
}

public Product getProduct() {
      return product;
}

public int getQuantity() {
      return quantity;
}

public Money getPrice() {
      return price;
}

public Money getSubTotal() {
      return subTotal;
}

public boolean isPriceValid() {
      return price.isGreaterThanZero() &&
          price.equals(product.getPrice()) &&
          price.multiply(quantity).equals(subTotal);
}

/** <h3>Q: Why not lombok for builder pattern?</h3>
 * <p>A: In order to minimise domain codes dependencies to just common objects and base classes</p>
 * <p>note, cannot use generic OrderItemId to set base Id, so it is replaced with OrderItemId in the builder</p>
 */
public static final class Builder {
      private OrderItemId orderItemId;
      private Product product;
      private int quantity;
      private Money price;
      private Money subTotal;

      private Builder() {
      }

      public static Builder builder() {
            return new Builder();
      }

      public Builder orderItemId(OrderItemId val) {
            orderItemId = val;
            return this;
      }

      public Builder product(Product val) {
            product = val;
            return this;
      }

      public Builder quantity(int val) {
            quantity = val;
            return this;
      }

      public Builder price(Money val) {
            price = val;
            return this;
      }

      public Builder subTotal(Money val) {
            subTotal = val;
            return this;
      }

      public OrderItem build() {
            return new OrderItem(this);
      }
}
}
