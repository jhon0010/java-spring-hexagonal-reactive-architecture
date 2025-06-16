package com.crm.validation.lead.infrastructure.adapter.in.web.envelopes;

public record ApiResponse<T>(
        boolean success,
        T data,
        ApiError error) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }
    public static <T> ApiResponse<T> fail(ApiError err) {
        return new ApiResponse<>(false, null, err);
    }
}
