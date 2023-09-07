package com.food.ordering.system.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Having Money as a value class ensures that it is dealt with in the same manner throughout the
 * application. This ensures the same scaling and rounding for all money objects.
 */
public class Money
{

    /**
     * Constant created for use in reduce method, this Money constant is used as the identity value ** for the
     * accumulator***:
     * validateItemsPrice()...orderItem ->
     * ...return orderItem.getSubTotal();
     * }).reduce(Money.ZERO**, Money::add***)
     */
    public static final Money ZERO = new Money(BigDecimal.ZERO);
    private final BigDecimal amount;

    /**
     * Input is first scaled to two decimal places and rounded using bankers method.
     *
     * @param input a BigDecimal that is treated as if it has not been scaled or rounded.
     */
    public Money(BigDecimal input)
    {
        this.amount = setScale(input);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return Objects.equals(amount, money.amount);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(amount);
    }


    /**
     * true give this.amount is greater than zero
     *
     * <p> this.amount.compareTo(BigDecimal.ZERO) returns -1, 0, 1 for less, equal, greater. It ignores scale.
     *
     * @return false if this amount is null or <= 0 else return true
     */
    public boolean isGreaterThanZero()
    {
        return Objects.nonNull(this.amount) && (this.amount.compareTo(BigDecimal.ZERO)) > 0;
    }

    /**
     * true give this.amount is greater money.amount
     *
     * <p> this.amount.compareTo(money.amount) returns -1, 0, 1 for less, equal, greater. It ignores scale.
     *
     * @return false if money amount is null or this.amount <= money.amount, else return true.
     */
    public boolean isGreaterThan(Money money)
    {
        return Objects.nonNull(this.amount) && (this.amount.compareTo(money.amount)) > 0;
    }

    /**
     * * <p>Question - when two amounts have and have no choice but to be scaled in the constructor, is it necessary
     * * to scale again - have done this here though</p>
     *
     * @param money a money object with an amount to be added to this Money object
     * @return a new money object containing the sum of both object's amounts.
     */
    public Money add(Money money)
    {
        return new Money(setScale(this.amount.add(money.amount)));
    }

    /**
     * @param money a money object with an amount to be subtracted from this Money object
     * @return a new money object containing the remainder of this objects amount after subtraction.
     */
    public Money subtract(Money money)
    {
        return new Money(setScale(this.amount.subtract(money.amount)));
    }

    /**
     * @param multiplier int representing e.g. the number of items at a specific value
     * @return new money object representing e.g. a total when a number of items at a specific value are subtotalled
     */
    public Money multiply(int multiplier)
    {
        return new Money(setScale(this.amount.multiply(new BigDecimal(multiplier))));
    }

    /**
     * <h3>Scale to two decimal points (transactional value) with HALF_EVEN rounding mode:
     *
     * <p>Behaves as for RoundingMode.HALF_UP if the digit to the left of the discarded fraction is odd;
     * behaves as for RoundingMode.HALF_DOWN if it's even.
     * <p>Note that this is the rounding mode that statistically minimizes cumulative error when applied repeatedly
     * over a sequence of calculations. It is sometimes known as "Banker's rounding".
     *
     * @param input is a base BigDecimal that may not have been scaled or rounded
     * @return a scaled BigDecimal rounded to two decimal points using mode that statistically minimizes cumulative
     * error.
     */
    private BigDecimal setScale(BigDecimal input)
    {
        return input.setScale(2, RoundingMode.HALF_EVEN);
    }

    public BigDecimal getAmount()
    {
        return amount;
    }


}
