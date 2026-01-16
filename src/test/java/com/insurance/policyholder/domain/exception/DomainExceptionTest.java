package com.insurance.policyholder.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Domain Exception Tests")
class DomainExceptionTest {

    @Nested
    @DisplayName("DomainException Base Class Tests")
    class DomainExceptionBaseTests {

        @Test
        @DisplayName("should create exception with message only")
        void shouldCreateExceptionWithMessageOnly() {
            DomainException ex = new DomainException("Test error message");

            assertEquals("Test error message", ex.getMessage());
            assertEquals("DOMAIN_ERROR", ex.getErrorCode());
        }

        @Test
        @DisplayName("should create exception with error code and message")
        void shouldCreateExceptionWithErrorCodeAndMessage() {
            DomainException ex = new DomainException("CUSTOM_ERROR", "Custom error message");

            assertEquals("Custom error message", ex.getMessage());
            assertEquals("CUSTOM_ERROR", ex.getErrorCode());
        }

        @Test
        @DisplayName("should create exception with error code, message and cause")
        void shouldCreateExceptionWithErrorCodeMessageAndCause() {
            Throwable cause = new RuntimeException("Original cause");
            DomainException ex = new DomainException("CUSTOM_ERROR", "Custom error message", cause);

            assertEquals("Custom error message", ex.getMessage());
            assertEquals("CUSTOM_ERROR", ex.getErrorCode());
            assertEquals(cause, ex.getCause());
        }

        @Test
        @DisplayName("should be instance of RuntimeException")
        void shouldBeInstanceOfRuntimeException() {
            DomainException ex = new DomainException("Test");
            assertTrue(ex instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("PolicyHolderNotFoundException Tests")
    class PolicyHolderNotFoundExceptionTests {

        @Test
        @DisplayName("should create exception with identifier")
        void shouldCreateExceptionWithIdentifier() {
            PolicyHolderNotFoundException ex = new PolicyHolderNotFoundException("PH0000000001");

            assertEquals("PolicyHolder not found: PH0000000001", ex.getMessage());
            assertEquals("POLICY_HOLDER_NOT_FOUND", ex.getErrorCode());
        }

        @Test
        @DisplayName("should create exception with identifier and custom message")
        void shouldCreateExceptionWithIdentifierAndCustomMessage() {
            PolicyHolderNotFoundException ex = new PolicyHolderNotFoundException(
                    "PH0000000001",
                    "Custom not found message"
            );

            assertEquals("Custom not found message", ex.getMessage());
            assertEquals("POLICY_HOLDER_NOT_FOUND", ex.getErrorCode());
        }

        @Test
        @DisplayName("should be instance of DomainException")
        void shouldBeInstanceOfDomainException() {
            PolicyHolderNotFoundException ex = new PolicyHolderNotFoundException("test");
            assertTrue(ex instanceof DomainException);
        }

        @Test
        @DisplayName("should work with national ID")
        void shouldWorkWithNationalId() {
            PolicyHolderNotFoundException ex = new PolicyHolderNotFoundException("A123456789");
            assertTrue(ex.getMessage().contains("A123456789"));
        }
    }

    @Nested
    @DisplayName("PolicyHolderNotActiveException Tests")
    class PolicyHolderNotActiveExceptionTests {

        @Test
        @DisplayName("should create exception with policy holder ID")
        void shouldCreateExceptionWithPolicyHolderId() {
            PolicyHolderNotActiveException ex = new PolicyHolderNotActiveException("PH0000000001");

            assertEquals("PolicyHolder is not active: PH0000000001", ex.getMessage());
            assertEquals("POLICY_HOLDER_NOT_ACTIVE", ex.getErrorCode());
        }

        @Test
        @DisplayName("should create exception with policy holder ID and custom message")
        void shouldCreateExceptionWithPolicyHolderIdAndCustomMessage() {
            PolicyHolderNotActiveException ex = new PolicyHolderNotActiveException(
                    "PH0000000001",
                    "Custom not active message"
            );

            assertEquals("Custom not active message", ex.getMessage());
            assertEquals("POLICY_HOLDER_NOT_ACTIVE", ex.getErrorCode());
        }

        @Test
        @DisplayName("should be instance of DomainException")
        void shouldBeInstanceOfDomainException() {
            PolicyHolderNotActiveException ex = new PolicyHolderNotActiveException("test");
            assertTrue(ex instanceof DomainException);
        }
    }

    @Nested
    @DisplayName("PolicyNotFoundException Tests")
    class PolicyNotFoundExceptionTests {

        @Test
        @DisplayName("should create exception with policy ID")
        void shouldCreateExceptionWithPolicyId() {
            PolicyNotFoundException ex = new PolicyNotFoundException("PO0000000001");

            assertEquals("Policy not found: PO0000000001", ex.getMessage());
            assertEquals("POLICY_NOT_FOUND", ex.getErrorCode());
        }

        @Test
        @DisplayName("should create exception with policy ID and custom message")
        void shouldCreateExceptionWithPolicyIdAndCustomMessage() {
            PolicyNotFoundException ex = new PolicyNotFoundException(
                    "PO0000000001",
                    "Custom policy not found message"
            );

            assertEquals("Custom policy not found message", ex.getMessage());
            assertEquals("POLICY_NOT_FOUND", ex.getErrorCode());
        }

        @Test
        @DisplayName("should be instance of DomainException")
        void shouldBeInstanceOfDomainException() {
            PolicyNotFoundException ex = new PolicyNotFoundException("test");
            assertTrue(ex instanceof DomainException);
        }
    }

    @Nested
    @DisplayName("Exception Hierarchy Tests")
    class ExceptionHierarchyTests {

        @Test
        @DisplayName("PolicyHolderNotFoundException should be caught as DomainException")
        void policyHolderNotFoundShouldBeCaughtAsDomainException() {
            try {
                throw new PolicyHolderNotFoundException("test");
            } catch (DomainException ex) {
                assertEquals("POLICY_HOLDER_NOT_FOUND", ex.getErrorCode());
            }
        }

        @Test
        @DisplayName("PolicyHolderNotActiveException should be caught as DomainException")
        void policyHolderNotActiveShouldBeCaughtAsDomainException() {
            try {
                throw new PolicyHolderNotActiveException("test");
            } catch (DomainException ex) {
                assertEquals("POLICY_HOLDER_NOT_ACTIVE", ex.getErrorCode());
            }
        }

        @Test
        @DisplayName("PolicyNotFoundException should be caught as DomainException")
        void policyNotFoundShouldBeCaughtAsDomainException() {
            try {
                throw new PolicyNotFoundException("test");
            } catch (DomainException ex) {
                assertEquals("POLICY_NOT_FOUND", ex.getErrorCode());
            }
        }

        @Test
        @DisplayName("all domain exceptions should be caught as RuntimeException")
        void allDomainExceptionsShouldBeCaughtAsRuntimeException() {
            assertThrows(RuntimeException.class, () -> {
                throw new PolicyHolderNotFoundException("test");
            });

            assertThrows(RuntimeException.class, () -> {
                throw new PolicyHolderNotActiveException("test");
            });

            assertThrows(RuntimeException.class, () -> {
                throw new PolicyNotFoundException("test");
            });
        }
    }
}
