package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import java.time.LocalDateTime;

/**
 * API 回應包裝器
 * 統一 API 回應格式
 *
 * @param <T> 回應資料類型
 */
public class ApiResponse<T> {

    private final boolean success;
    private final T data;
    private final String message;
    private final LocalDateTime timestamp;

    private ApiResponse(boolean success, T data, String message) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * 建立成功回應
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /**
     * 建立成功回應（含訊息）
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }

    /**
     * 建立失敗回應
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
