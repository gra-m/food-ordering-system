package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
private final int quantity;
private String name;
private Money price;
private boolean available;

private Product(Builder builder) {
    setId(builder.productId);
    name = builder.name;
    price = builder.price;
    quantity = builder.quantity;
    available = builder.available;
}

public static Builder builder() {
    return Builder.builder();
}

public void updateWithConfirmedNamePriceAndAvailability(String name, Money price, boolean available) {
    this.name = name;
    this.price = price;
    this.available = available;
}


public String getName() {
    return name;
}

public Money getPrice() {
    return price;
}

public int getQuantity() {
    return quantity;
}

public boolean isAvailable() {
    return available;
}


/**
 * {@code Product} builder static inner class.
 */
public static final class Builder {

    private ProductId productId;
    private String name;
    private Money price;
    private int quantity;
    private boolean available;

    private Builder() {
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Sets the {@code id} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code id} to set
     * @return a reference to this Builder
     */
    public Builder productId(ProductId val) {
        productId = val;
        return this;
    }

    /**
     * Sets the {@code name} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code name} to set
     * @return a reference to this Builder
     */
    public Builder name(String val) {
        name = val;
        return this;
    }

    /**
     * Sets the {@code price} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code price} to set
     * @return a reference to this Builder
     */
    public Builder price(Money val) {
        price = val;
        return this;
    }

    /**
     * Sets the {@code quantity} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code quantity} to set
     * @return a reference to this Builder
     */
    public Builder quantity(int val) {
        quantity = val;
        return this;
    }

    /**
     * Sets the {@code available} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code available} to set
     * @return a reference to this Builder
     */
    public Builder available(boolean val) {
        available = val;
        return this;
    }

    /**
     * Returns a {@code Product} built from the parameters previously set.
     *
     * @return a {@code Product} built with parameters of this {@code Product.Builder}
     */
    public Product build() {
        return new Product(this);
    }


}


}
