package com.crm.validation.lead.domain;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.LeadValidations;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO: Validate DDD Aggregate is this the correct way?
 */
@AllArgsConstructor
@Data
public class LeadValidationResult {
    Lead lead;
    LeadValidations validations;
}
