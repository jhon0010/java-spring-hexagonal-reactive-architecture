package com.crm.validation.lead.domain;

import com.crm.validation.lead.application.services.validator.ValidationResult;
import com.crm.validation.lead.domain.model.Lead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * TODO: Validate DDD Aggregate is this the correct way?
 */
@Builder
@AllArgsConstructor
@Data
public class LeadValidationResult {
    Lead lead;
    ValidationResult validations;
}
