package com.insurance.policyholder.domain.model.entity;

import com.insurance.policyholder.domain.model.enums.PolicyStatus;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Policy Entity Tests")
class PolicyTest {

    @Nested
    @DisplayName("建立保單")
    class CreatePolicyTests {

        @Test
        @DisplayName("應成功建立保單")
        void shouldCreatePolicySuccessfully() {
            // Given
            PolicyType type = PolicyType.LIFE;
            Money premium = Money.twd(10000);
            Money sumInsured = Money.twd(1000000);
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusYears(1);

            // When
            Policy policy = Policy.create(type, premium, sumInsured, startDate, endDate);

            // Then
            assertNotNull(policy);
            assertNotNull(policy.getId());
            assertEquals(type, policy.getPolicyType());
            assertEquals(premium, policy.getPremium());
            assertEquals(sumInsured, policy.getSumInsured());
            assertEquals(startDate, policy.getStartDate());
            assertEquals(endDate, policy.getEndDate());
            assertEquals(PolicyStatus.ACTIVE, policy.getStatus());
        }

        @Test
        @DisplayName("保單編號應自動生成")
        void shouldGeneratePolicyId() {
            // Given
            Policy policy1 = Policy.create(
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );
            Policy policy2 = Policy.create(
                    PolicyType.HEALTH,
                    Money.twd(5000),
                    Money.twd(500000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );

            // Then
            assertNotNull(policy1.getId());
            assertNotNull(policy2.getId());
            assertNotEquals(policy1.getId().getValue(), policy2.getId().getValue());
        }

        @Test
        @DisplayName("保單編號應為 PO 開頭")
        void shouldHaveCorrectIdFormat() {
            // Given
            Policy policy = Policy.create(
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );

            // Then
            assertTrue(policy.getId().getValue().startsWith("PO"));
        }
    }

    @Nested
    @DisplayName("日期驗證")
    class DateValidationTests {

        @Test
        @DisplayName("結束日期不能早於開始日期")
        void shouldThrowExceptionWhenEndDateBeforeStartDate() {
            // Given
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.minusDays(1);

            // When & Then
            assertThrows(IllegalArgumentException.class, () ->
                    Policy.create(
                            PolicyType.LIFE,
                            Money.twd(10000),
                            Money.twd(1000000),
                            startDate,
                            endDate
                    )
            );
        }

        @Test
        @DisplayName("開始日期不能為空")
        void shouldThrowExceptionWhenStartDateIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    Policy.create(
                            PolicyType.LIFE,
                            Money.twd(10000),
                            Money.twd(1000000),
                            null,
                            LocalDate.now().plusYears(1)
                    )
            );
        }

        @Test
        @DisplayName("結束日期不能為空")
        void shouldThrowExceptionWhenEndDateIsNull() {
            assertThrows(IllegalArgumentException.class, () ->
                    Policy.create(
                            PolicyType.LIFE,
                            Money.twd(10000),
                            Money.twd(1000000),
                            LocalDate.now(),
                            null
                    )
            );
        }
    }

    @Nested
    @DisplayName("保單狀態")
    class PolicyStatusTests {

        @Test
        @DisplayName("新建保單應為 ACTIVE 狀態")
        void newPolicyShouldBeActive() {
            // Given
            Policy policy = Policy.create(
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );

            // Then
            assertTrue(policy.isActive());
            assertEquals(PolicyStatus.ACTIVE, policy.getStatus());
        }

        @Test
        @DisplayName("應可終止保單")
        void shouldTerminatePolicy() {
            // Given
            Policy policy = Policy.create(
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );

            // When
            policy.terminate();

            // Then
            assertFalse(policy.isActive());
            assertEquals(PolicyStatus.TERMINATED, policy.getStatus());
        }

        @Test
        @DisplayName("已終止的保單不能再次終止")
        void shouldNotTerminateAlreadyTerminatedPolicy() {
            // Given
            Policy policy = Policy.create(
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );
            policy.terminate();

            // When & Then
            assertThrows(IllegalStateException.class, policy::terminate);
        }
    }

    @Nested
    @DisplayName("有效期檢查")
    class ValidPeriodTests {

        @Test
        @DisplayName("日期在有效期內應回傳 true")
        void shouldReturnTrueWhenDateWithinValidPeriod() {
            // Given
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 12, 31);
            Policy policy = Policy.create(
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    startDate,
                    endDate
            );

            // Then
            assertTrue(policy.isWithinValidPeriod(LocalDate.of(2024, 6, 15)));
            assertTrue(policy.isWithinValidPeriod(startDate));
            assertTrue(policy.isWithinValidPeriod(endDate));
        }

        @Test
        @DisplayName("日期在有效期外應回傳 false")
        void shouldReturnFalseWhenDateOutsideValidPeriod() {
            // Given
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2024, 12, 31);
            Policy policy = Policy.create(
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    startDate,
                    endDate
            );

            // Then
            assertFalse(policy.isWithinValidPeriod(LocalDate.of(2023, 12, 31)));
            assertFalse(policy.isWithinValidPeriod(LocalDate.of(2025, 1, 1)));
        }
    }
}
