package com.food.ordering.system.payment.service.domain.entity;

import com.food.ordering.system.domain.entity.BaseEntity;
import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.domain.valueobject.Money;
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId;
import com.food.ordering.system.payment.service.domain.valueobject.TransactionType;

public class CreditHistory extends BaseEntity<CreditHistoryId> {

    private final CustomerId customerId;
    private final Money amount;
    private final TransactionType transactionType;

private CreditHistory(Builder builder) {
    super.setId(builder.creditHistoryId);
    customerId = builder.customerId;
    amount = builder.amount;
    transactionType = builder.transactionType;
}


public CustomerId getCustomerId() {
    return customerId;
}

public Money getAmount() {
    return amount;
}

public TransactionType getTransactionType() {
    return transactionType;
}

/**
 * {@code CreditHistory} builder static inner class.
 */
public static final class Builder {
    private CreditHistoryId creditHistoryId;
    private CustomerId customerId;
    private Money amount;
    private TransactionType transactionType;

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
    public Builder creditHistoryId(CreditHistoryId val) {
        creditHistoryId = val;
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
     * Sets the {@code amount} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code amount} to set
     * @return a reference to this Builder
     */
    public Builder amount(Money val) {
        amount = val;
        return this;
    }

    /**
     * Sets the {@code transactionType} and returns a reference to this Builder enabling method chaining.
     *
     * @param val the {@code transactionType} to set
     * @return a reference to this Builder
     */
    public Builder transactionType(TransactionType val) {
        transactionType = val;
        return this;
    }


    /**
     * Returns a {@code CreditHistory} built from the parameters previously set.
     *
     * @return a {@code CreditHistory} built with parameters of this {@code CreditHistory.Builder}
     */
    public CreditHistory build() {
        return new CreditHistory(this);
    }
}
}
