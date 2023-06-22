package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.OrderApprovalStatus;
import com.food.ordering.system.domain.valueobject.OrderId;
import com.food.ordering.system.domain.valueobject.RestaurantId;
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId;

public class OrderApproval extends BaseEntity<OrderApprovalId> {

private final RestaurantId restaurantId;
private final OrderId orderId;
private final OrderApprovalStatus approvalStatus;

private OrderApproval(Builder builder) {
    super.setId(builder.orderApprovalId);
    restaurantId = builder.restaurantId;
    orderId = builder.orderId;
    approvalStatus = builder.approvalStatus;
}

public OrderApproval(RestaurantId restaurantId, OrderId orderId, OrderApprovalStatus approvalStatus) {
    this.restaurantId = restaurantId;
    this.orderId = orderId;
    this.approvalStatus = approvalStatus;
}

public static Builder builder() {
    return new Builder();
}

public RestaurantId getRestaurantId() {
    return restaurantId;
}

public OrderId getOrderId() {
    return orderId;
}

public OrderApprovalStatus getApprovalStatus() {
    return approvalStatus;
}


/**
 * {@code OrderApproval} builder static inner class.
 */
public static final class Builder {

    private OrderApprovalId orderApprovalId;
    private RestaurantId restaurantId;
    private OrderId orderId;
    private OrderApprovalStatus approvalStatus;

    private Builder() {
    }


    /**
     * Sets the {@code id} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code id} to set
     * @return a reference to this Builder
     */
    public Builder orderApprovalId(OrderApprovalId val) {
        orderApprovalId = val;
        return this;
    }

    /**
     * Sets the {@code restaurantId} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code restaurantId} to set
     * @return a reference to this Builder
     */
    public Builder restaurantId(RestaurantId val) {
        restaurantId = val;
        return this;
    }

    /**
     * Sets the {@code orderId} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code orderId} to set
     * @return a reference to this Builder
     */
    public Builder orderId(OrderId val) {
        orderId = val;
        return this;
    }

    /**
     * Sets the {@code approvalStatus} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code approvalStatus} to set
     * @return a reference to this Builder
     */
    public Builder approvalStatus(OrderApprovalStatus val) {
        approvalStatus = val;
        return this;
    }

    /**
     * Returns a {@code OrderApproval} built from the parameters previously set.
     *
     * @return a {@code OrderApproval} built with parameters of this {@code OrderApproval.Builder}
     */
    public OrderApproval build() {
        return new OrderApproval(this);
    }


}


}
