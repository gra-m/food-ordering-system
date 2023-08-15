package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.RestaurantId;

import java.util.List;

public class Restaurant extends AggregateRoot<RestaurantId> {
private final List<Product> products;
private boolean active;

private Restaurant(Builder builder) {
    super.setId(builder.restaurantId);
    products = builder.products;
    active = builder.active;
}

public static Builder builder() {
    return new Builder();
}

public List<Product> getProducts() {
    return products;
}

public boolean isActive() {
    return active;
}

/**
 * {@code Restaurant} builder static inner class.
 */
public static final class Builder {
    private RestaurantId restaurantId;
    private List<Product> products;
    private boolean active;

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
     * Returns a {@code Restaurant} built from the parameters previously set.
     *
     * @return a {@code Restaurant} built with parameters of this {@code Restaurant.Builder}
     */
    public Restaurant build() {
        return new Restaurant(this);
    }


}


}
