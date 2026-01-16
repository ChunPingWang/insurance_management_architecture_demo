package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 錯誤回應
 * 用於 API 錯誤訊息的標準格式
 */
public class ErrorResponse {

    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;
    private final List<FieldError> fieldErrors;

    private ErrorResponse(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
        this.fieldErrors = fieldErrors != null ? fieldErrors : new ArrayList<>();
    }

    /**
     * 建立錯誤回應
     */
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, null);
    }

    /**
     * 建立包含欄位錯誤的回應
     */
    public static ErrorResponse of(int status, String error, String message, String path, List<FieldError> fieldErrors) {
        return new ErrorResponse(status, error, message, path, fieldErrors);
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    /**
     * 欄位錯誤
     */
    public static class FieldError {
        private final String field;
        private final String message;
        private final Object rejectedValue;

        public FieldError(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }
    }
}
