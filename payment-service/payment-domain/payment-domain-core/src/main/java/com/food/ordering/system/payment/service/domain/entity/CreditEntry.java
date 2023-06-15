package com.food.ordering.system.payment.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.payment.service.domain.valueobject.CreditEntryId;

/**
 * Could be future rest api for Credit Entry separate from Payment
 */
public class CreditEntry extends BaseEntity<CreditEntryId> {

    private final CustomerId customerId;
    private Money totalCreditAmount;

    public void addCreditAmount(Money amount){
        totalCreditAmount = totalCreditAmount.add(amount);
    }

    public void subtractCreditAmount(Money amount) {
        totalCreditAmount = totalCreditAmount.subtract(amount);
    }

private CreditEntry(Builder builder) {
    super.setId(builder.creditEntryId);
    customerId = builder.customerId;
    totalCreditAmount = builder.totalCreditAmount;
}


public CustomerId getCustomerId() {
    return customerId;
}

public Money getTotalCreditAmount() {
    return totalCreditAmount;
}

/**
 * {@code CreditEntry} builder static inner class.
 */
public static final class Builder {
    private CreditEntryId creditEntryId;
    private CustomerId customerId;
    private Money totalCreditAmount;

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
    public Builder creditEntryId(CreditEntryId val) {
        creditEntryId = val;
        return this;
    }

    /**
     * Sets the {@code customerId} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code customerId} to set
     * @return a reference to this Builder
     */
    public Builder customerId(CustomerId val) {
        customerId = val;
        return this;
    }

    /**
     * Sets the {@code totalCreditAmount} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code totalCreditAmount} to set
     * @return a reference to this Builder
     */
    public Builder totalCreditAmount(Money val) {
        totalCreditAmount = val;
        return this;
    }

    /**
     * Returns a {@code CreditEntry} built from the parameters previously set.
     *
     * @return a {@code CreditEntry} built with parameters of this {@code CreditEntry.Builder}
     */
    public CreditEntry build() {
        return new CreditEntry(this);
    }
}
}
