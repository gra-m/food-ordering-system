package com.food.ordering.system.restaurant.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.domain.valueobject.ProductId;

<<<<<<< Updated upstream
public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;
    private final int quantity;
    private boolean available;

private Product(Builder builder) {
    super.setId(Builder.builder().productId);
    name = builder.name;
    price = builder.price;
    quantity = builder.quantity;
    available = builder.available;
}

public static Builder builder(){
    return Builder.builder();
}

=======
public class Product  {
private final ProductId id;
private final int quantity;
private String name;
private Money price;
private boolean available;

>>>>>>> Stashed changes
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

public ProductId getId() {return id;}

public boolean isAvailable() {
    return available;
}

// in debug product Id is null when it gets to this constructor it is not null before.(In builder..)
private Product(Builder builder) {
    id = builder.productId;
    name = builder.name;
    price = builder.price;
    quantity = builder.quantity;
    available = builder.available;
}

public static Builder builder() {
    return new Builder();
}

public static final class Builder {
    private ProductId productId;
    private String name;
    private Money price;
    private int quantity;
    private boolean available;

    private Builder() {
    }

    public Builder productId(ProductId val) {
        productId = val;
        return this;
    }

    public Builder name(String val) {
        name = val;
        return this;
    }

    public Builder price(Money val) {
        price = val;
        return this;
    }

    public Builder quantity(int val) {
        quantity = val;
        return this;
    }

    public Builder available(boolean val) {
        available = val;
        return this;
    }

    public Product build() {
        return new Product(this);
    }


}


}
