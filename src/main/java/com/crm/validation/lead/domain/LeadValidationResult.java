package com.crm.validation.lead.domain;

import com.crm.validation.lead.application.services.validator.ValidationResults;
import com.crm.validation.lead.domain.model.Lead;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

/**
 * This class represents the outcome of a Lead validation process.
 * In DDD terms, this is not a true Aggregate Root, but rather a Value Object
 * that contains the results of a domain operation.
 *
 * It's immutable and holds both the Lead entity (which may be considered an Aggregate Root)
 * and the validation results associated with that Lead.
 */
@Builder
@AllArgsConstructor
@Value
public class LeadValidationResult {
    Lead lead;
    ValidationResults validations;

    /**
     * Creates a new LeadValidationResult with a different Lead instance.
     * Since this is a Value Object, we create a new instance rather than modifying the existing one.
     *
     * @param newLead The new Lead to associate with the validation results
     * @return A new LeadValidationResult instance with the updated Lead
     */
    public LeadValidationResult withLead(Lead newLead) {
        return LeadValidationResult.builder()
                .lead(newLead)
                .validations(this.validations)
                .build();
    }

    /**
     * Creates a new LeadValidationResult with different validation results.
     * Since this is a Value Object, we create a new instance rather than modifying the existing one.
     *
     * @param newValidations The new validation results
     * @return A new LeadValidationResult instance with the updated validation results
     */
    public LeadValidationResult withValidations(ValidationResults newValidations) {
        return LeadValidationResult.builder()
                .lead(this.lead)
                .validations(newValidations)
                .build();
    }
}
