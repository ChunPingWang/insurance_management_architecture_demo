package com.insurance.policyholder.domain.model.valueobject;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * 金額值物件
 */
public final class Money {

    private static final Currency DEFAULT_CURRENCY = Currency.getInstance("TWD");

    private final BigDecimal amount;
    private final Currency currency;

    private Money(BigDecimal amount, Currency currency) {
        this.amount = amount;
        this.currency = currency;
    }

    /**
     * 建立金額（預設新台幣）
     */
    public static Money of(BigDecimal amount) {
        return of(amount, DEFAULT_CURRENCY);
    }

    /**
     * 建立金額（指定幣別）
     */
    public static Money of(BigDecimal amount, Currency currency) {
        validate(amount);
        return new Money(amount, currency);
    }

    /**
     * 建立新台幣金額（便利方法）
     */
    public static Money twd(long amount) {
        return of(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }

    /**
     * 零元
     */
    public static Money zero() {
        return of(BigDecimal.ZERO);
    }

    private static void validate(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    /**
     * 加法
     */
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * 減法
     */
    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * 是否大於
     */
    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * 是否小於
     */
    public boolean isLessThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                    "Cannot operate on different currencies: " + this.currency + " and " + other.currency);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 &&
                Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return currency.getSymbol() + " " + amount;
    }
}
