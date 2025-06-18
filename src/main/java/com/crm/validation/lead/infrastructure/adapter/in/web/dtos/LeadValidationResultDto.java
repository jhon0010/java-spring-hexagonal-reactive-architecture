package com.crm.validation.lead.infrastructure.adapter.in.web.dtos;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * DTO to represent a lead validation result to be returned to the client.
 * This prevents domain objects from crossing the adapter boundary.
 */
public record LeadValidationResultDto(
        // Lead information
        UUID id,
        String name,
        LocalDate birthdate,
        String email,
        String phoneNumber,
        String documentType,
        int documentNumber,
        String state,

        // Validation information
        boolean isValid,
        List<String> validationErrors
) {
}
