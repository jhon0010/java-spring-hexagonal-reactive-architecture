package com.crm.validation.lead.domain;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.LeadValidations;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class LeadValidationResult {
    Lead lead;
    LeadValidations validations;
}
