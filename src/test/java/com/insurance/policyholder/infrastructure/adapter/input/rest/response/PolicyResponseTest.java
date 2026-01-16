package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PolicyResponse Tests")
class PolicyResponseTest {

    private static final String ID = "PO0000000001";
    private static final String POLICY_HOLDER_ID = "PH0000000001";
    private static final String POLICY_TYPE = "LIFE";
    private static final BigDecimal PREMIUM = new BigDecimal("10000.00");
    private static final BigDecimal SUM_INSURED = new BigDecimal("1000000.00");
    private static final LocalDate START_DATE = LocalDate.of(2024, 1, 1);
    private static final LocalDate END_DATE = LocalDate.of(2025, 1, 1);
    private static final String STATUS = "ACTIVE";

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create response with default constructor")
        void shouldCreateResponseWithDefaultConstructor() {
            PolicyResponse response = new PolicyResponse();

            assertNull(response.getId());
            assertNull(response.getPolicyHolderId());
            assertNull(response.getPolicyType());
            assertNull(response.getPremium());
            assertNull(response.getSumInsured());
            assertNull(response.getStartDate());
            assertNull(response.getEndDate());
            assertNull(response.getStatus());
        }
    }

    @Nested
    @DisplayName("Basic Property Setter Tests")
    class BasicPropertySetterTests {

        @Test
        @DisplayName("should set id")
        void shouldSetId() {
            PolicyResponse response = new PolicyResponse();

            response.setId(ID);

            assertEquals(ID, response.getId());
        }

        @Test
        @DisplayName("should set policy holder id")
        void shouldSetPolicyHolderId() {
            PolicyResponse response = new PolicyResponse();

            response.setPolicyHolderId(POLICY_HOLDER_ID);

            assertEquals(POLICY_HOLDER_ID, response.getPolicyHolderId());
        }

        @Test
        @DisplayName("should set policy type")
        void shouldSetPolicyType() {
            PolicyResponse response = new PolicyResponse();

            response.setPolicyType(POLICY_TYPE);

            assertEquals(POLICY_TYPE, response.getPolicyType());
        }

        @Test
        @DisplayName("should set premium")
        void shouldSetPremium() {
            PolicyResponse response = new PolicyResponse();

            response.setPremium(PREMIUM);

            assertEquals(PREMIUM, response.getPremium());
        }

        @Test
        @DisplayName("should set sum insured")
        void shouldSetSumInsured() {
            PolicyResponse response = new PolicyResponse();

            response.setSumInsured(SUM_INSURED);

            assertEquals(SUM_INSURED, response.getSumInsured());
        }

        @Test
        @DisplayName("should set start date")
        void shouldSetStartDate() {
            PolicyResponse response = new PolicyResponse();

            response.setStartDate(START_DATE);

            assertEquals(START_DATE, response.getStartDate());
        }

        @Test
        @DisplayName("should set end date")
        void shouldSetEndDate() {
            PolicyResponse response = new PolicyResponse();

            response.setEndDate(END_DATE);

            assertEquals(END_DATE, response.getEndDate());
        }

        @Test
        @DisplayName("should set status")
        void shouldSetStatus() {
            PolicyResponse response = new PolicyResponse();

            response.setStatus(STATUS);

            assertEquals(STATUS, response.getStatus());
        }
    }

    @Nested
    @DisplayName("Policy Type Tests")
    class PolicyTypeTests {

        @Test
        @DisplayName("should handle LIFE policy type")
        void shouldHandleLifePolicyType() {
            PolicyResponse response = new PolicyResponse();

            response.setPolicyType("LIFE");

            assertEquals("LIFE", response.getPolicyType());
        }

        @Test
        @DisplayName("should handle HEALTH policy type")
        void shouldHandleHealthPolicyType() {
            PolicyResponse response = new PolicyResponse();

            response.setPolicyType("HEALTH");

            assertEquals("HEALTH", response.getPolicyType());
        }

        @Test
        @DisplayName("should handle ACCIDENT policy type")
        void shouldHandleAccidentPolicyType() {
            PolicyResponse response = new PolicyResponse();

            response.setPolicyType("ACCIDENT");

            assertEquals("ACCIDENT", response.getPolicyType());
        }

        @Test
        @DisplayName("should handle PROPERTY policy type")
        void shouldHandlePropertyPolicyType() {
            PolicyResponse response = new PolicyResponse();

            response.setPolicyType("PROPERTY");

            assertEquals("PROPERTY", response.getPolicyType());
        }
    }

    @Nested
    @DisplayName("Status Tests")
    class StatusTests {

        @Test
        @DisplayName("should handle ACTIVE status")
        void shouldHandleActiveStatus() {
            PolicyResponse response = new PolicyResponse();

            response.setStatus("ACTIVE");

            assertEquals("ACTIVE", response.getStatus());
        }

        @Test
        @DisplayName("should handle LAPSED status")
        void shouldHandleLapsedStatus() {
            PolicyResponse response = new PolicyResponse();

            response.setStatus("LAPSED");

            assertEquals("LAPSED", response.getStatus());
        }

        @Test
        @DisplayName("should handle CANCELLED status")
        void shouldHandleCancelledStatus() {
            PolicyResponse response = new PolicyResponse();

            response.setStatus("CANCELLED");

            assertEquals("CANCELLED", response.getStatus());
        }

        @Test
        @DisplayName("should handle EXPIRED status")
        void shouldHandleExpiredStatus() {
            PolicyResponse response = new PolicyResponse();

            response.setStatus("EXPIRED");

            assertEquals("EXPIRED", response.getStatus());
        }
    }

    @Nested
    @DisplayName("Premium and Sum Insured Tests")
    class MoneyTests {

        @Test
        @DisplayName("should handle zero premium")
        void shouldHandleZeroPremium() {
            PolicyResponse response = new PolicyResponse();

            response.setPremium(BigDecimal.ZERO);

            assertEquals(BigDecimal.ZERO, response.getPremium());
        }

        @Test
        @DisplayName("should handle large premium")
        void shouldHandleLargePremium() {
            PolicyResponse response = new PolicyResponse();
            BigDecimal largePremium = new BigDecimal("999999999.99");

            response.setPremium(largePremium);

            assertEquals(largePremium, response.getPremium());
        }

        @Test
        @DisplayName("should handle large sum insured")
        void shouldHandleLargeSumInsured() {
            PolicyResponse response = new PolicyResponse();
            BigDecimal largeSumInsured = new BigDecimal("9999999999.99");

            response.setSumInsured(largeSumInsured);

            assertEquals(largeSumInsured, response.getSumInsured());
        }

        @Test
        @DisplayName("should handle decimal precision")
        void shouldHandleDecimalPrecision() {
            PolicyResponse response = new PolicyResponse();
            BigDecimal premiumWithDecimals = new BigDecimal("12345.67");

            response.setPremium(premiumWithDecimals);

            assertEquals(premiumWithDecimals, response.getPremium());
        }
    }

    @Nested
    @DisplayName("Date Tests")
    class DateTests {

        @Test
        @DisplayName("should handle same start and end date")
        void shouldHandleSameStartAndEndDate() {
            PolicyResponse response = new PolicyResponse();
            LocalDate sameDate = LocalDate.of(2024, 6, 15);

            response.setStartDate(sameDate);
            response.setEndDate(sameDate);

            assertEquals(sameDate, response.getStartDate());
            assertEquals(sameDate, response.getEndDate());
        }

        @Test
        @DisplayName("should handle long term policy dates")
        void shouldHandleLongTermPolicyDates() {
            PolicyResponse response = new PolicyResponse();
            LocalDate startDate = LocalDate.of(2024, 1, 1);
            LocalDate endDate = LocalDate.of(2054, 1, 1); // 30 years

            response.setStartDate(startDate);
            response.setEndDate(endDate);

            assertEquals(startDate, response.getStartDate());
            assertEquals(endDate, response.getEndDate());
        }

        @Test
        @DisplayName("should handle null dates")
        void shouldHandleNullDates() {
            PolicyResponse response = new PolicyResponse();

            response.setStartDate(null);
            response.setEndDate(null);

            assertNull(response.getStartDate());
            assertNull(response.getEndDate());
        }
    }

    @Nested
    @DisplayName("Full Response Tests")
    class FullResponseTests {

        @Test
        @DisplayName("should set all properties correctly")
        void shouldSetAllPropertiesCorrectly() {
            // Given
            PolicyResponse response = new PolicyResponse();

            // When
            response.setId(ID);
            response.setPolicyHolderId(POLICY_HOLDER_ID);
            response.setPolicyType(POLICY_TYPE);
            response.setPremium(PREMIUM);
            response.setSumInsured(SUM_INSURED);
            response.setStartDate(START_DATE);
            response.setEndDate(END_DATE);
            response.setStatus(STATUS);

            // Then
            assertEquals(ID, response.getId());
            assertEquals(POLICY_HOLDER_ID, response.getPolicyHolderId());
            assertEquals(POLICY_TYPE, response.getPolicyType());
            assertEquals(PREMIUM, response.getPremium());
            assertEquals(SUM_INSURED, response.getSumInsured());
            assertEquals(START_DATE, response.getStartDate());
            assertEquals(END_DATE, response.getEndDate());
            assertEquals(STATUS, response.getStatus());
        }
    }

    @Nested
    @DisplayName("ID Format Tests")
    class IdFormatTests {

        @Test
        @DisplayName("should store policy ID with correct format")
        void shouldStorePolicyIdWithCorrectFormat() {
            PolicyResponse response = new PolicyResponse();

            response.setId("PO9999999999");

            assertEquals("PO9999999999", response.getId());
        }

        @Test
        @DisplayName("should store policy holder ID with correct format")
        void shouldStorePolicyHolderIdWithCorrectFormat() {
            PolicyResponse response = new PolicyResponse();

            response.setPolicyHolderId("PH9999999999");

            assertEquals("PH9999999999", response.getPolicyHolderId());
        }
    }
}
