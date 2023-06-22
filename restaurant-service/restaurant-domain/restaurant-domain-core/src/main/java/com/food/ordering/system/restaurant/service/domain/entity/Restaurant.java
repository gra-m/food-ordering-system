package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.system.domain.valueobject.OrderStatus;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;

import java.util.List;
import java.util.UUID;

public class Restaurant extends AggregateRoot<RestaurantId> {
private final OrderDetail orderDetail;
private OrderApproval orderApproval;
private boolean active;

private Restaurant(Builder builder) {
    super.setId(builder.restaurantId);
    orderApproval = builder.orderApproval;
    active = builder.active;
    orderDetail = builder.orderDetail;
}

public static Builder builder() {
    return new Builder();
}

/**
 * Given that an order is @ OrderStatus.PAID and all products required to fulfill order are available and the confirmed
 * and expected order totals are equal no failure messages will be added.
 *
 * @param failureMessages
 */
public void validateOrder(List<String> failureMessages) {

    if( orderDetail.getOrderStatus() != OrderStatus.PAID ) {
        failureMessages.add(String.format("Payment is not completed for order: %s", orderDetail.getId().getValue()));
    }

    Money totalAmount = orderDetail.getProducts().stream().map(product -> {
        if( !product.isAvailable() ) {
            failureMessages.add(String.format("Product with id: %s is not available", product.getId().getValue()));
        }
        return product.getPrice().multiply(product.getQuantity());
    }).reduce(Money.ZERO, (money, money2) -> money.add(money2));

    if( !totalAmount.equals(orderDetail.getTotalAmount()) ) {
        failureMessages.add(String.format("Price total is not correct for order: %s", orderDetail.getId()));
    }

}

public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {

    this.orderApproval = OrderApproval
    .builder()
    .orderApprovalId(new OrderApprovalId(UUID.randomUUID()))
    .restaurantId(this.getId())
    .orderId(this.getOrderDetail().getId())
    .approvalStatus(orderApprovalStatus)
    .build();

}

public void setActive(boolean active) {
    this.active = active;
}

public OrderApproval getOrderApproval() {
    return orderApproval;
}

public boolean isActive() {
    return active;
}

public OrderDetail getOrderDetail() {
    return orderDetail;
}


/**
 * {@code Restaurant} builder static inner class.
 */
public static final class Builder {

    private RestaurantId restaurantId;
    private OrderApproval orderApproval;
    private boolean active;
    private OrderDetail orderDetail;

    private Builder() {
    }

    /**
     * Sets the {@code id} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code id} to set
     * @return a reference to this Builder
     */
    public Builder restaurantId(RestaurantId val) {
        restaurantId = val;
        return this;
    }

    /**
     * Sets the {@code orderApproval} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code orderApproval} to set
     * @return a reference to this Builder
     */
    public Builder orderApproval(OrderApproval val) {
        orderApproval = val;
        return this;
    }

    /**
     * Sets the {@code active} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code active} to set
     * @return a reference to this Builder
     */
    public Builder active(boolean val) {
        active = val;
        return this;
    }

    /**
     * Sets the {@code orderDetail} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code orderDetail} to set
     * @return a reference to this Builder
     */
    public Builder orderDetail(OrderDetail val) {
        orderDetail = val;
        return this;
    }

    /**
     * Returns a {@code Restaurant} built from the parameters previously set.
     *
     * @return a {@code Restaurant} built with parameters of this {@code Restaurant.Builder}
     */
    public Restaurant build() {
        return new Restaurant(this);
    }


}


}
