package com.insurance.policyholder.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Nested
    @DisplayName("建立測試")
    class CreationTests {

        @Test
        @DisplayName("應成功建立有效的 Money")
        void shouldCreateValidMoney() {
            Money money = Money.of(new BigDecimal("50000.00"));
            assertNotNull(money);
            assertEquals(new BigDecimal("50000.00"), money.getAmount());
            assertEquals(Currency.getInstance("TWD"), money.getCurrency());
        }

        @Test
        @DisplayName("應成功建立指定幣別的 Money")
        void shouldCreateMoneyWithSpecificCurrency() {
            Money money = Money.of(new BigDecimal("1000.00"), Currency.getInstance("USD"));
            assertEquals(Currency.getInstance("USD"), money.getCurrency());
        }

        @Test
        @DisplayName("使用 TWD 便利方法建立")
        void shouldCreateTwdMoney() {
            Money money = Money.twd(50000);
            assertEquals(new BigDecimal("50000"), money.getAmount());
            assertEquals(Currency.getInstance("TWD"), money.getCurrency());
        }
    }

    @Nested
    @DisplayName("驗證測試")
    class ValidationTests {

        @Test
        @DisplayName("金額為 null 應拋出例外")
        void shouldThrowExceptionForNullAmount() {
            assertThrows(IllegalArgumentException.class, () -> Money.of(null));
        }

        @Test
        @DisplayName("負數金額應拋出例外")
        void shouldThrowExceptionForNegativeAmount() {
            assertThrows(IllegalArgumentException.class,
                    () -> Money.of(new BigDecimal("-100.00")));
        }

        @Test
        @DisplayName("零金額應成功建立")
        void shouldCreateZeroMoney() {
            Money money = Money.of(BigDecimal.ZERO);
            assertEquals(BigDecimal.ZERO, money.getAmount());
        }
    }

    @Nested
    @DisplayName("運算測試")
    class OperationTests {

        @Test
        @DisplayName("加法運算應正確")
        void shouldAddCorrectly() {
            Money money1 = Money.twd(1000);
            Money money2 = Money.twd(500);
            Money result = money1.add(money2);
            assertEquals(new BigDecimal("1500"), result.getAmount());
        }

        @Test
        @DisplayName("減法運算應正確")
        void shouldSubtractCorrectly() {
            Money money1 = Money.twd(1000);
            Money money2 = Money.twd(300);
            Money result = money1.subtract(money2);
            assertEquals(new BigDecimal("700"), result.getAmount());
        }

        @Test
        @DisplayName("不同幣別加法應拋出例外")
        void shouldThrowExceptionForDifferentCurrencies() {
            Money twd = Money.twd(1000);
            Money usd = Money.of(new BigDecimal("100"), Currency.getInstance("USD"));
            assertThrows(IllegalArgumentException.class, () -> twd.add(usd));
        }
    }

    @Nested
    @DisplayName("比較測試")
    class ComparisonTests {

        @Test
        @DisplayName("應正確比較金額大小")
        void shouldCompareCorrectly() {
            Money money1 = Money.twd(1000);
            Money money2 = Money.twd(500);
            assertTrue(money1.isGreaterThan(money2));
            assertFalse(money1.isLessThan(money2));
        }
    }

    @Nested
    @DisplayName("相等性測試")
    class EqualityTests {

        @Test
        @DisplayName("相同金額和幣別應相等")
        void shouldBeEqualForSameAmountAndCurrency() {
            Money money1 = Money.twd(1000);
            Money money2 = Money.twd(1000);
            assertEquals(money1, money2);
            assertEquals(money1.hashCode(), money2.hashCode());
        }

        @Test
        @DisplayName("不同金額應不相等")
        void shouldNotBeEqualForDifferentAmount() {
            Money money1 = Money.twd(1000);
            Money money2 = Money.twd(2000);
            assertNotEquals(money1, money2);
        }
    }
}
