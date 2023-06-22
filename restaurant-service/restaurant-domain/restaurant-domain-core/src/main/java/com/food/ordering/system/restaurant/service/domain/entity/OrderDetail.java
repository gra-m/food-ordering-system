package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.OrderStatus;

import java.util.List;

public class OrderDetail extends BaseEntity<OrderId> {
private final List<Product> products;
private OrderStatus orderStatus;
private Money totalAmount;

private OrderDetail(Builder builder) {
    super.setId(builder.orderId);
    orderStatus = builder.orderStatus;
    totalAmount = builder.totalAmount;
    products = builder.products;
}

public static Builder builder() {
    return new Builder();
}


public OrderStatus getOrderStatus() {
    return orderStatus;
}

public Money getTotalAmount() {
    return totalAmount;
}

public List<Product> getProducts() {
    return products;
}


/**
 * {@code OrderDetail} builder static inner class.
 */
public static final class Builder {

    private OrderId orderId;
    private OrderStatus orderStatus;
    private Money totalAmount;
    private List<Product> products;

    private Builder() {
    }

    /**
     * Sets the {@code id} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code id} to set
     * @return a reference to this Builder
     */
    public Builder orderId(OrderId val) {
        orderId = val;
        return this;
    }

    /**
     * Sets the {@code orderStatus} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code orderStatus} to set
     * @return a reference to this Builder
     */
    public Builder orderStatus(OrderStatus val) {
        orderStatus = val;
        return this;
    }

    /**
     * Sets the {@code totalAmount} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code totalAmount} to set
     * @return a reference to this Builder
     */
    public Builder totalAmount(Money val) {
        totalAmount = val;
        return this;
    }

    /**
     * Sets the {@code products} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code products} to set
     * @return a reference to this Builder
     */
    public Builder products(List<Product> val) {
        products = val;
        return this;
    }

    /**
     * Returns a {@code OrderDetail} built from the parameters previously set.
     *
     * @return a {@code OrderDetail} built with parameters of this {@code OrderDetail.Builder}
     */
    public OrderDetail build() {
        return new OrderDetail(this);
    }


}


}
