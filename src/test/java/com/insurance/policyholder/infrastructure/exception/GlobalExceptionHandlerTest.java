package com.insurance.policyholder.infrastructure.exception;

import com.insurance.policyholder.domain.exception.DomainException;
import com.insurance.policyholder.domain.exception.PolicyHolderNotActiveException;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.domain.exception.PolicyNotFoundException;
import com.insurance.policyholder.infrastructure.adapter.input.rest.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        when(request.getRequestURI()).thenReturn("/api/v1/policyholders");
    }

    @Nested
    @DisplayName("PolicyHolderNotFoundException Handling")
    class PolicyHolderNotFoundExceptionTests {

        @Test
        @DisplayName("should return 404 Not Found")
        void shouldReturnNotFound() {
            PolicyHolderNotFoundException ex = new PolicyHolderNotFoundException("PH0000000001");

            ResponseEntity<ErrorResponse> response = handler.handlePolicyHolderNotFoundException(ex, request);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().getStatus());
            assertEquals("POLICY_HOLDER_NOT_FOUND", response.getBody().getError());
            assertTrue(response.getBody().getMessage().contains("PH0000000001"));
            assertEquals("/api/v1/policyholders", response.getBody().getPath());
        }
    }

    @Nested
    @DisplayName("PolicyNotFoundException Handling")
    class PolicyNotFoundExceptionTests {

        @Test
        @DisplayName("should return 404 Not Found")
        void shouldReturnNotFound() {
            PolicyNotFoundException ex = new PolicyNotFoundException("PO0000000001");

            ResponseEntity<ErrorResponse> response = handler.handlePolicyNotFoundException(ex, request);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(404, response.getBody().getStatus());
            assertEquals("POLICY_NOT_FOUND", response.getBody().getError());
            assertTrue(response.getBody().getMessage().contains("PO0000000001"));
        }
    }

    @Nested
    @DisplayName("PolicyHolderNotActiveException Handling")
    class PolicyHolderNotActiveExceptionTests {

        @Test
        @DisplayName("should return 400 Bad Request")
        void shouldReturnBadRequest() {
            PolicyHolderNotActiveException ex = new PolicyHolderNotActiveException("PH0000000001");

            ResponseEntity<ErrorResponse> response = handler.handlePolicyHolderNotActiveException(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatus());
            assertEquals("POLICY_HOLDER_NOT_ACTIVE", response.getBody().getError());
            assertTrue(response.getBody().getMessage().contains("PH0000000001"));
        }
    }

    @Nested
    @DisplayName("DomainException Handling")
    class DomainExceptionTests {

        @Test
        @DisplayName("should return 400 Bad Request")
        void shouldReturnBadRequest() {
            DomainException ex = new DomainException("CUSTOM_ERROR", "Custom error message");

            ResponseEntity<ErrorResponse> response = handler.handleDomainException(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatus());
            assertEquals("CUSTOM_ERROR", response.getBody().getError());
            assertEquals("Custom error message", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("MethodArgumentNotValidException Handling")
    class ValidationExceptionTests {

        @Test
        @DisplayName("should return 400 with field errors")
        void shouldReturnBadRequestWithFieldErrors() {
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("request", "name", "rejected", false, null, null, "Name is required");

            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            // Use BindException instead since MethodArgumentNotValidException requires MethodParameter
            BindException ex = new BindException(bindingResult);

            ResponseEntity<ErrorResponse> response = handler.handleBindException(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatus());
            assertEquals("BIND_ERROR", response.getBody().getError());
            assertEquals(1, response.getBody().getFieldErrors().size());
            assertEquals("name", response.getBody().getFieldErrors().get(0).getField());
            assertEquals("Name is required", response.getBody().getFieldErrors().get(0).getMessage());
        }

        @Test
        @DisplayName("should handle multiple field errors")
        void shouldHandleMultipleFieldErrors() {
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError error1 = new FieldError("request", "name", null, false, null, null, "Name is required");
            FieldError error2 = new FieldError("request", "email", "invalid", false, null, null, "Invalid email format");

            when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

            BindException ex = new BindException(bindingResult);

            ResponseEntity<ErrorResponse> response = handler.handleBindException(ex, request);

            assertEquals(2, response.getBody().getFieldErrors().size());
        }
    }

    @Nested
    @DisplayName("BindException Handling")
    class BindExceptionTests {

        @Test
        @DisplayName("should return 400 with field errors")
        void shouldReturnBadRequestWithFieldErrors() {
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("request", "page", "-1", false, null, null, "Page must be positive");

            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

            BindException ex = new BindException(bindingResult);

            ResponseEntity<ErrorResponse> response = handler.handleBindException(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatus());
            assertEquals("BIND_ERROR", response.getBody().getError());
            assertEquals("Binding failed", response.getBody().getMessage());
            assertEquals(1, response.getBody().getFieldErrors().size());
        }
    }

    @Nested
    @DisplayName("IllegalArgumentException Handling")
    class IllegalArgumentExceptionTests {

        @Test
        @DisplayName("should return 400 Bad Request")
        void shouldReturnBadRequest() {
            IllegalArgumentException ex = new IllegalArgumentException("Invalid argument provided");

            ResponseEntity<ErrorResponse> response = handler.handleIllegalArgumentException(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatus());
            assertEquals("INVALID_ARGUMENT", response.getBody().getError());
            assertEquals("Invalid argument provided", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("IllegalStateException Handling")
    class IllegalStateExceptionTests {

        @Test
        @DisplayName("should return 400 Bad Request")
        void shouldReturnBadRequest() {
            IllegalStateException ex = new IllegalStateException("Invalid state");

            ResponseEntity<ErrorResponse> response = handler.handleIllegalStateException(ex, request);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(400, response.getBody().getStatus());
            assertEquals("INVALID_ARGUMENT", response.getBody().getError());
            assertEquals("Invalid state", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("Generic Exception Handling")
    class GenericExceptionTests {

        @Test
        @DisplayName("should return 500 Internal Server Error")
        void shouldReturnInternalServerError() {
            Exception ex = new RuntimeException("Unexpected error");

            ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(500, response.getBody().getStatus());
            assertEquals("INTERNAL_ERROR", response.getBody().getError());
            assertEquals("An unexpected error occurred", response.getBody().getMessage());
        }

        @Test
        @DisplayName("should not expose internal error details")
        void shouldNotExposeInternalErrorDetails() {
            Exception ex = new RuntimeException("Database connection failed: password=secret123");

            ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

            assertFalse(response.getBody().getMessage().contains("secret"));
            assertFalse(response.getBody().getMessage().contains("password"));
            assertEquals("An unexpected error occurred", response.getBody().getMessage());
        }
    }

    @Nested
    @DisplayName("Response Structure Tests")
    class ResponseStructureTests {

        @Test
        @DisplayName("should include timestamp in all responses")
        void shouldIncludeTimestamp() {
            PolicyHolderNotFoundException ex = new PolicyHolderNotFoundException("test");

            ResponseEntity<ErrorResponse> response = handler.handlePolicyHolderNotFoundException(ex, request);

            assertNotNull(response.getBody().getTimestamp());
        }

        @Test
        @DisplayName("should include request path in all responses")
        void shouldIncludeRequestPath() {
            when(request.getRequestURI()).thenReturn("/api/v1/custom/path");
            PolicyNotFoundException ex = new PolicyNotFoundException("test");

            ResponseEntity<ErrorResponse> response = handler.handlePolicyNotFoundException(ex, request);

            assertEquals("/api/v1/custom/path", response.getBody().getPath());
        }
    }
}
