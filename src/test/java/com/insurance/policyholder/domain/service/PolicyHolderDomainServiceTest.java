package com.insurance.policyholder.domain.service;

import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * PolicyHolderDomainService 單元測試
 */
@DisplayName("PolicyHolderDomainService Tests")
class PolicyHolderDomainServiceTest {

    private PolicyHolderDomainService domainService;

    @BeforeEach
    void setUp() {
        domainService = new PolicyHolderDomainService();
    }

    @Nested
    @DisplayName("isAdult - 年齡驗證")
    class IsAdultTests {

        @Test
        @DisplayName("年滿 18 歲應回傳 true")
        void shouldReturnTrueFor18YearsOld() {
            LocalDate eighteenYearsAgo = LocalDate.now().minusYears(18);
            assertThat(domainService.isAdult(eighteenYearsAgo)).isTrue();
        }

        @Test
        @DisplayName("超過 18 歲應回傳 true")
        void shouldReturnTrueForOver18() {
            LocalDate thirtyYearsAgo = LocalDate.now().minusYears(30);
            assertThat(domainService.isAdult(thirtyYearsAgo)).isTrue();
        }

        @Test
        @DisplayName("未滿 18 歲應回傳 false")
        void shouldReturnFalseForUnder18() {
            LocalDate seventeenYearsAgo = LocalDate.now().minusYears(17);
            assertThat(domainService.isAdult(seventeenYearsAgo)).isFalse();
        }

        @Test
        @DisplayName("null 出生日期應回傳 false")
        void shouldReturnFalseForNullBirthDate() {
            assertThat(domainService.isAdult(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("calculateAge - 年齡計算")
    class CalculateAgeTests {

        @Test
        @DisplayName("應正確計算年齡")
        void shouldCalculateAgeCorrectly() {
            LocalDate twentyYearsAgo = LocalDate.now().minusYears(20);
            assertThat(domainService.calculateAge(twentyYearsAgo)).isEqualTo(20);
        }

        @Test
        @DisplayName("出生日期為 null 應拋出例外")
        void shouldThrowExceptionForNullBirthDate() {
            assertThatThrownBy(() -> domainService.calculateAge(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Birth date cannot be null");
        }
    }

    @Nested
    @DisplayName("canAddPolicy - 新增保單權限")
    class CanAddPolicyTests {

        @Test
        @DisplayName("ACTIVE 狀態可新增保單")
        void shouldAllowAddPolicyForActiveStatus() {
            assertThat(domainService.canAddPolicy(PolicyHolderStatus.ACTIVE)).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 狀態不可新增保單")
        void shouldDenyAddPolicyForInactiveStatus() {
            assertThat(domainService.canAddPolicy(PolicyHolderStatus.INACTIVE)).isFalse();
        }

        @Test
        @DisplayName("SUSPENDED 狀態不可新增保單")
        void shouldDenyAddPolicyForSuspendedStatus() {
            assertThat(domainService.canAddPolicy(PolicyHolderStatus.SUSPENDED)).isFalse();
        }
    }

    @Nested
    @DisplayName("canUpdate - 更新權限")
    class CanUpdateTests {

        @Test
        @DisplayName("ACTIVE 狀態可更新")
        void shouldAllowUpdateForActiveStatus() {
            assertThat(domainService.canUpdate(PolicyHolderStatus.ACTIVE)).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 狀態不可更新")
        void shouldDenyUpdateForInactiveStatus() {
            assertThat(domainService.canUpdate(PolicyHolderStatus.INACTIVE)).isFalse();
        }

        @Test
        @DisplayName("SUSPENDED 狀態不可更新")
        void shouldDenyUpdateForSuspendedStatus() {
            assertThat(domainService.canUpdate(PolicyHolderStatus.SUSPENDED)).isFalse();
        }
    }

    @Nested
    @DisplayName("canDeactivate - 停用權限")
    class CanDeactivateTests {

        @Test
        @DisplayName("ACTIVE 狀態可停用")
        void shouldAllowDeactivateForActiveStatus() {
            assertThat(domainService.canDeactivate(PolicyHolderStatus.ACTIVE)).isTrue();
        }

        @Test
        @DisplayName("INACTIVE 狀態不可再停用")
        void shouldDenyDeactivateForInactiveStatus() {
            assertThat(domainService.canDeactivate(PolicyHolderStatus.INACTIVE)).isFalse();
        }

        @Test
        @DisplayName("SUSPENDED 狀態不可停用")
        void shouldDenyDeactivateForSuspendedStatus() {
            assertThat(domainService.canDeactivate(PolicyHolderStatus.SUSPENDED)).isFalse();
        }
    }

    @Nested
    @DisplayName("isValidNationalIdFormat - 身分證字號格式驗證")
    class IsValidNationalIdFormatTests {

        @Test
        @DisplayName("有效身分證字號應回傳 true")
        void shouldReturnTrueForValidNationalId() {
            assertThat(domainService.isValidNationalIdFormat("A123456789")).isTrue();
        }

        @Test
        @DisplayName("無效身分證字號應回傳 false")
        void shouldReturnFalseForInvalidNationalId() {
            assertThat(domainService.isValidNationalIdFormat("A000000000")).isFalse();
        }

        @Test
        @DisplayName("null 應回傳 false")
        void shouldReturnFalseForNull() {
            assertThat(domainService.isValidNationalIdFormat(null)).isFalse();
        }

        @Test
        @DisplayName("長度不足應回傳 false")
        void shouldReturnFalseForShortId() {
            assertThat(domainService.isValidNationalIdFormat("A12345")).isFalse();
        }
    }

    @Nested
    @DisplayName("isValidPolicyPeriod - 保單期間驗證")
    class IsValidPolicyPeriodTests {

        @Test
        @DisplayName("有效期間應回傳 true")
        void shouldReturnTrueForValidPeriod() {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().plusYears(1);
            assertThat(domainService.isValidPolicyPeriod(startDate, endDate)).isTrue();
        }

        @Test
        @DisplayName("null 到期日（終身險）應回傳 true")
        void shouldReturnTrueForNullEndDate() {
            LocalDate startDate = LocalDate.now();
            assertThat(domainService.isValidPolicyPeriod(startDate, null)).isTrue();
        }

        @Test
        @DisplayName("生效日早於今日應回傳 false")
        void shouldReturnFalseForPastStartDate() {
            LocalDate startDate = LocalDate.now().minusDays(1);
            LocalDate endDate = LocalDate.now().plusYears(1);
            assertThat(domainService.isValidPolicyPeriod(startDate, endDate)).isFalse();
        }

        @Test
        @DisplayName("到期日早於生效日應回傳 false")
        void shouldReturnFalseForEndDateBeforeStartDate() {
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = LocalDate.now().minusDays(1);
            assertThat(domainService.isValidPolicyPeriod(startDate, endDate)).isFalse();
        }

        @Test
        @DisplayName("null 生效日應回傳 false")
        void shouldReturnFalseForNullStartDate() {
            assertThat(domainService.isValidPolicyPeriod(null, LocalDate.now())).isFalse();
        }
    }

    @Nested
    @DisplayName("isValidPremiumAmount - 保費金額驗證")
    class IsValidPremiumAmountTests {

        @Test
        @DisplayName("正數金額應回傳 true")
        void shouldReturnTrueForPositiveAmount() {
            assertThat(domainService.isValidPremiumAmount(new BigDecimal("10000"))).isTrue();
        }

        @Test
        @DisplayName("零金額應回傳 false")
        void shouldReturnFalseForZeroAmount() {
            assertThat(domainService.isValidPremiumAmount(BigDecimal.ZERO)).isFalse();
        }

        @Test
        @DisplayName("負數金額應回傳 false")
        void shouldReturnFalseForNegativeAmount() {
            assertThat(domainService.isValidPremiumAmount(new BigDecimal("-100"))).isFalse();
        }

        @Test
        @DisplayName("null 應回傳 false")
        void shouldReturnFalseForNull() {
            assertThat(domainService.isValidPremiumAmount(null)).isFalse();
        }
    }

    @Nested
    @DisplayName("isValidSumInsured - 保險金額驗證")
    class IsValidSumInsuredTests {

        @Test
        @DisplayName("正數金額應回傳 true")
        void shouldReturnTrueForPositiveAmount() {
            assertThat(domainService.isValidSumInsured(new BigDecimal("1000000"))).isTrue();
        }

        @Test
        @DisplayName("零金額應回傳 false")
        void shouldReturnFalseForZeroAmount() {
            assertThat(domainService.isValidSumInsured(BigDecimal.ZERO)).isFalse();
        }

        @Test
        @DisplayName("負數金額應回傳 false")
        void shouldReturnFalseForNegativeAmount() {
            assertThat(domainService.isValidSumInsured(new BigDecimal("-100"))).isFalse();
        }

        @Test
        @DisplayName("null 應回傳 false")
        void shouldReturnFalseForNull() {
            assertThat(domainService.isValidSumInsured(null)).isFalse();
        }
    }
}
