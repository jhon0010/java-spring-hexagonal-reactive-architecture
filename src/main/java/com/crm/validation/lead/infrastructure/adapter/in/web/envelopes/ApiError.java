package com.crm.validation.lead.infrastructure.adapter.in.web.envelopes;

import java.util.List;

public record ApiError(
        String code,
        String message,
        List<String> details) {

    public static ApiError of(String code, String message) {
        return new ApiError(code, message, List.of());
    }

}
