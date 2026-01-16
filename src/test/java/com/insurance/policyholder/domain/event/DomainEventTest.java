package com.insurance.policyholder.domain.event;

import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.entity.Policy;
import com.insurance.policyholder.domain.model.enums.Gender;
import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.enums.PolicyType;
import com.insurance.policyholder.domain.model.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Event Tests")
class DomainEventTest {

    private static final String POLICY_HOLDER_ID = "PH0000000001";
    private static final String NATIONAL_ID = "A123456789";
    private static final String NAME = "John Doe";
    private static final String MOBILE_PHONE = "0912345678";
    private static final String EMAIL = "john@example.com";
    private static final String ZIP_CODE = "10001";
    private static final String CITY = "Taipei";
    private static final String DISTRICT = "Xinyi";
    private static final String STREET = "123 Test Street";

    private PolicyHolder policyHolder;

    @BeforeEach
    void setUp() {
        policyHolder = PolicyHolder.reconstitute(
                PolicyHolderId.of(POLICY_HOLDER_ID),
                NationalId.of(NATIONAL_ID),
                PersonalInfo.of(NAME, Gender.MALE, LocalDate.of(1990, 1, 15)),
                ContactInfo.of(MOBILE_PHONE, EMAIL),
                Address.of(ZIP_CODE, CITY, DISTRICT, STREET),
                PolicyHolderStatus.ACTIVE,
                1L
        );
    }

    @Nested
    @DisplayName("PolicyHolderCreated Event Tests")
    class PolicyHolderCreatedTests {

        @Test
        @DisplayName("should create event with correct attributes")
        void shouldCreateEventWithCorrectAttributes() {
            PolicyHolderCreated event = new PolicyHolderCreated(
                    POLICY_HOLDER_ID,
                    NATIONAL_ID,
                    NAME,
                    "MALE",
                    LocalDate.of(1990, 1, 15),
                    MOBILE_PHONE,
                    EMAIL,
                    "Taipei Xinyi 123 Test Street"
            );

            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
            assertEquals(POLICY_HOLDER_ID, event.getAggregateId());
            assertEquals("PolicyHolder", event.getAggregateType());
            assertEquals("PolicyHolderCreated", event.getEventType());
            assertEquals(NATIONAL_ID, event.getNationalId());
            assertEquals(NAME, event.getName());
            assertEquals("MALE", event.getGender());
            assertEquals(LocalDate.of(1990, 1, 15), event.getBirthDate());
            assertEquals(MOBILE_PHONE, event.getMobilePhone());
            assertEquals(EMAIL, event.getEmail());
        }

        @Test
        @DisplayName("should generate unique event ID")
        void shouldGenerateUniqueEventId() {
            PolicyHolderCreated event1 = new PolicyHolderCreated(
                    POLICY_HOLDER_ID, NATIONAL_ID, NAME, "MALE",
                    LocalDate.of(1990, 1, 15), MOBILE_PHONE, EMAIL, "address"
            );
            PolicyHolderCreated event2 = new PolicyHolderCreated(
                    POLICY_HOLDER_ID, NATIONAL_ID, NAME, "MALE",
                    LocalDate.of(1990, 1, 15), MOBILE_PHONE, EMAIL, "address"
            );

            assertNotEquals(event1.getEventId(), event2.getEventId());
        }

        @Test
        @DisplayName("should have proper toString representation")
        void shouldHaveProperToString() {
            PolicyHolderCreated event = new PolicyHolderCreated(
                    POLICY_HOLDER_ID, NATIONAL_ID, NAME, "MALE",
                    LocalDate.of(1990, 1, 15), MOBILE_PHONE, EMAIL, "address"
            );

            String str = event.toString();
            assertTrue(str.contains("PolicyHolderCreated"));
            assertTrue(str.contains(POLICY_HOLDER_ID));
            assertTrue(str.contains(NATIONAL_ID));
            assertTrue(str.contains(NAME));
        }
    }

    @Nested
    @DisplayName("PolicyHolderUpdated Event Tests")
    class PolicyHolderUpdatedTests {

        @Test
        @DisplayName("should create event from PolicyHolder")
        void shouldCreateEventFromPolicyHolder() {
            PolicyHolderUpdated event = new PolicyHolderUpdated(policyHolder);

            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
            assertEquals(POLICY_HOLDER_ID, event.getAggregateId());
            assertEquals("PolicyHolder", event.getAggregateType());
            assertEquals("PolicyHolderUpdated", event.getEventType());
            assertEquals(MOBILE_PHONE, event.getMobilePhone());
            assertEquals(EMAIL, event.getEmail());
            assertEquals(ZIP_CODE, event.getZipCode());
            assertEquals(CITY, event.getCity());
            assertEquals(DISTRICT, event.getDistrict());
            assertEquals(STREET, event.getStreet());
            assertEquals(1L, event.getVersion());
        }

        @Test
        @DisplayName("should capture updated contact info")
        void shouldCaptureUpdatedContactInfo() {
            String newMobile = "0987654321";
            String newEmail = "updated@example.com";
            policyHolder.updateContactInfo(ContactInfo.of(newMobile, newEmail));

            PolicyHolderUpdated event = new PolicyHolderUpdated(policyHolder);

            assertEquals(newMobile, event.getMobilePhone());
            assertEquals(newEmail, event.getEmail());
        }

        @Test
        @DisplayName("should capture updated address")
        void shouldCaptureUpdatedAddress() {
            String newCity = "Kaohsiung";
            String newDistrict = "Sanmin";
            policyHolder.updateAddress(Address.of("80000", newCity, newDistrict, "New Street"));

            PolicyHolderUpdated event = new PolicyHolderUpdated(policyHolder);

            assertEquals("80000", event.getZipCode());
            assertEquals(newCity, event.getCity());
            assertEquals(newDistrict, event.getDistrict());
            assertEquals("New Street", event.getStreet());
        }
    }

    @Nested
    @DisplayName("PolicyHolderDeleted Event Tests")
    class PolicyHolderDeletedTests {

        @Test
        @DisplayName("should create event from PolicyHolder")
        void shouldCreateEventFromPolicyHolder() {
            PolicyHolderDeleted event = new PolicyHolderDeleted(policyHolder);

            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
            assertEquals(POLICY_HOLDER_ID, event.getAggregateId());
            assertEquals("PolicyHolder", event.getAggregateType());
            assertEquals("PolicyHolderDeleted", event.getEventType());
            assertEquals(NAME, event.getName());
        }

        @Test
        @DisplayName("should mask national ID")
        void shouldMaskNationalId() {
            PolicyHolderDeleted event = new PolicyHolderDeleted(policyHolder);

            String maskedId = event.getNationalId();
            // NationalId.getMasked() should return masked format like "A12***6789"
            assertTrue(maskedId.contains("*"));
            assertNotEquals(NATIONAL_ID, maskedId);
        }
    }

    @Nested
    @DisplayName("PolicyAdded Event Tests")
    class PolicyAddedTests {

        private Policy policy;

        @BeforeEach
        void setUp() {
            policy = Policy.create(
                    PolicyType.LIFE,
                    Money.twd(10000),
                    Money.twd(1000000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(1)
            );
        }

        @Test
        @DisplayName("should create event with policy data")
        void shouldCreateEventWithPolicyData() {
            PolicyAdded event = new PolicyAdded(POLICY_HOLDER_ID, policy);

            assertNotNull(event.getEventId());
            assertNotNull(event.getOccurredOn());
            assertEquals(POLICY_HOLDER_ID, event.getAggregateId());
            assertEquals("PolicyHolder", event.getAggregateType());
            assertEquals("PolicyAdded", event.getEventType());
            assertEquals(policy.getId().getValue(), event.getPolicyId());
            assertEquals("LIFE", event.getPolicyType());
            assertEquals(new BigDecimal("10000"), event.getPremium());
            assertEquals(new BigDecimal("1000000"), event.getSumInsured());
            assertEquals(LocalDate.now(), event.getStartDate());
            assertEquals(LocalDate.now().plusYears(1), event.getEndDate());
            assertEquals("ACTIVE", event.getStatus());
        }

        @Test
        @DisplayName("should capture different policy types")
        void shouldCaptureDifferentPolicyTypes() {
            Policy healthPolicy = Policy.create(
                    PolicyType.HEALTH,
                    Money.twd(5000),
                    Money.twd(500000),
                    LocalDate.now(),
                    LocalDate.now().plusYears(2)
            );

            PolicyAdded event = new PolicyAdded(POLICY_HOLDER_ID, healthPolicy);

            assertEquals("HEALTH", event.getPolicyType());
        }
    }
}
