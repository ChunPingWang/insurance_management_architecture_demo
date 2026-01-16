package com.insurance.policyholder.application.readmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PolicyReadModel Tests")
class PolicyReadModelTest {

    private static final String POLICY_ID = "PO0000000001";
    private static final String POLICY_HOLDER_ID = "PH0000000001";
    private static final String POLICY_TYPE = "LIFE";
    private static final BigDecimal PREMIUM = new BigDecimal("10000");
    private static final BigDecimal SUM_INSURED = new BigDecimal("1000000");
    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2025, 1, 1);
    private static final String STATUS = "ACTIVE";

    private PolicyReadModel createFullModel() {
        return new PolicyReadModel(
                POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                PREMIUM, SUM_INSURED, START_DATE, END_DATE, STATUS
        );
    }

    @Nested
    @DisplayName("Constructor and Basic Properties Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create read model with all properties")
        void shouldCreateReadModelWithAllProperties() {
            // When
            PolicyReadModel model = createFullModel();

            // Then
            assertEquals(POLICY_ID, model.getId());
            assertEquals(POLICY_HOLDER_ID, model.getPolicyHolderId());
            assertEquals(POLICY_TYPE, model.getPolicyType());
            assertEquals(PREMIUM, model.getPremium());
            assertEquals(SUM_INSURED, model.getSumInsured());
            assertEquals(START_DATE, model.getStartDate());
            assertEquals(END_DATE, model.getEndDate());
            assertEquals(STATUS, model.getStatus());
        }

        @Test
        @DisplayName("should handle null values")
        void shouldHandleNullValues() {
            // When
            PolicyReadModel model = new PolicyReadModel(
                    null, null, null, null, null, null, null, null
            );

            // Then
            assertNull(model.getId());
            assertNull(model.getPolicyHolderId());
            assertNull(model.getPolicyType());
            assertNull(model.getPremium());
            assertNull(model.getSumInsured());
            assertNull(model.getStartDate());
            assertNull(model.getEndDate());
            assertNull(model.getStatus());
        }
    }

    @Nested
    @DisplayName("Policy Type Tests")
    class PolicyTypeTests {

        @Test
        @DisplayName("should handle LIFE policy type")
        void shouldHandleLifePolicyType() {
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, "LIFE",
                    PREMIUM, SUM_INSURED, START_DATE, END_DATE, STATUS
            );
            assertEquals("LIFE", model.getPolicyType());
        }

        @Test
        @DisplayName("should handle HEALTH policy type")
        void shouldHandleHealthPolicyType() {
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, "HEALTH",
                    PREMIUM, SUM_INSURED, START_DATE, END_DATE, STATUS
            );
            assertEquals("HEALTH", model.getPolicyType());
        }

        @Test
        @DisplayName("should handle ACCIDENT policy type")
        void shouldHandleAccidentPolicyType() {
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, "ACCIDENT",
                    PREMIUM, SUM_INSURED, START_DATE, END_DATE, STATUS
            );
            assertEquals("ACCIDENT", model.getPolicyType());
        }

        @Test
        @DisplayName("should handle PROPERTY policy type")
        void shouldHandlePropertyPolicyType() {
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, "PROPERTY",
                    PREMIUM, SUM_INSURED, START_DATE, END_DATE, STATUS
            );
            assertEquals("PROPERTY", model.getPolicyType());
        }
    }

    @Nested
    @DisplayName("Status Tests")
    class StatusTests {

        @Test
        @DisplayName("should handle ACTIVE status")
        void shouldHandleActiveStatus() {
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    PREMIUM, SUM_INSURED, START_DATE, END_DATE, "ACTIVE"
            );
            assertEquals("ACTIVE", model.getStatus());
        }

        @Test
        @DisplayName("should handle LAPSED status")
        void shouldHandleLapsedStatus() {
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    PREMIUM, SUM_INSURED, START_DATE, END_DATE, "LAPSED"
            );
            assertEquals("LAPSED", model.getStatus());
        }

        @Test
        @DisplayName("should handle CANCELLED status")
        void shouldHandleCancelledStatus() {
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    PREMIUM, SUM_INSURED, START_DATE, END_DATE, "CANCELLED"
            );
            assertEquals("CANCELLED", model.getStatus());
        }

        @Test
        @DisplayName("should handle EXPIRED status")
        void shouldHandleExpiredStatus() {
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    PREMIUM, SUM_INSURED, START_DATE, END_DATE, "EXPIRED"
            );
            assertEquals("EXPIRED", model.getStatus());
        }
    }

    @Nested
    @DisplayName("Premium and Sum Insured Tests")
    class MoneyTests {

        @Test
        @DisplayName("should store premium correctly")
        void shouldStorePremiumCorrectly() {
            BigDecimal premium = new BigDecimal("25000.50");
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    premium, SUM_INSURED, START_DATE, END_DATE, STATUS
            );
            assertEquals(premium, model.getPremium());
        }

        @Test
        @DisplayName("should store sum insured correctly")
        void shouldStoreSumInsuredCorrectly() {
            BigDecimal sumInsured = new BigDecimal("5000000");
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    PREMIUM, sumInsured, START_DATE, END_DATE, STATUS
            );
            assertEquals(sumInsured, model.getSumInsured());
        }

        @Test
        @DisplayName("should handle zero premium")
        void shouldHandleZeroPremium() {
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    BigDecimal.ZERO, SUM_INSURED, START_DATE, END_DATE, STATUS
            );
            assertEquals(BigDecimal.ZERO, model.getPremium());
        }

        @Test
        @DisplayName("should handle large amounts")
        void shouldHandleLargeAmounts() {
            BigDecimal largePremium = new BigDecimal("999999999.99");
            BigDecimal largeSumInsured = new BigDecimal("9999999999.99");
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    largePremium, largeSumInsured, START_DATE, END_DATE, STATUS
            );
            assertEquals(largePremium, model.getPremium());
            assertEquals(largeSumInsured, model.getSumInsured());
        }
    }

    @Nested
    @DisplayName("Date Tests")
    class DateTests {

        @Test
        @DisplayName("should store start date correctly")
        void shouldStoreStartDateCorrectly() {
            LocalDate startDate = LocalDate.of(2024, 6, 15);
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    PREMIUM, SUM_INSURED, startDate, END_DATE, STATUS
            );
            assertEquals(startDate, model.getStartDate());
        }

        @Test
        @DisplayName("should store end date correctly")
        void shouldStoreEndDateCorrectly() {
            LocalDate endDate = LocalDate.of(2030, 12, 31);
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    PREMIUM, SUM_INSURED, START_DATE, endDate, STATUS
            );
            assertEquals(endDate, model.getEndDate());
        }

        @Test
        @DisplayName("should handle same start and end date")
        void shouldHandleSameStartAndEndDate() {
            LocalDate sameDate = LocalDate.of(2024, 1, 1);
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    PREMIUM, SUM_INSURED, sameDate, sameDate, STATUS
            );
            assertEquals(sameDate, model.getStartDate());
            assertEquals(sameDate, model.getEndDate());
        }

        @Test
        @DisplayName("should handle long term policy dates")
        void shouldHandleLongTermPolicyDates() {
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2054, 1, 1); // 30 year policy
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, POLICY_HOLDER_ID, POLICY_TYPE,
                    PREMIUM, SUM_INSURED, startDate, endDate, STATUS
            );
            assertEquals(startDate, model.getStartDate());
            assertEquals(endDate, model.getEndDate());
        }
    }

    @Nested
    @DisplayName("ID Tests")
    class IdTests {

        @Test
        @DisplayName("should store policy ID correctly")
        void shouldStorePolicyIdCorrectly() {
            PolicyReadModel model = new PolicyReadModel(
                    "PO9999999999", POLICY_HOLDER_ID, POLICY_TYPE,
                    PREMIUM, SUM_INSURED, START_DATE, END_DATE, STATUS
            );
            assertEquals("PO9999999999", model.getId());
        }

        @Test
        @DisplayName("should store policy holder ID correctly")
        void shouldStorePolicyHolderIdCorrectly() {
            PolicyReadModel model = new PolicyReadModel(
                    POLICY_ID, "PH9999999999", POLICY_TYPE,
                    PREMIUM, SUM_INSURED, START_DATE, END_DATE, STATUS
            );
            assertEquals("PH9999999999", model.getPolicyHolderId());
        }
    }
}
