package com.crm.validation.lead.objectmother;

import com.crm.validation.lead.application.services.validator.ValidationOutcome;
import com.crm.validation.lead.application.services.validator.ValidationResults;
import com.crm.validation.lead.domain.model.enums.DocumentType;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadEntity;

import java.time.LocalDate;
import java.util.Arrays;

public class LeadObjectMother {

    public static LeadDto createValidLeadDto() {
        return LeadDto.builder()
                .name("John Doe")
                .email("jhon.doe@gmail.com")
                .phoneNumber("+1234567890")
                .documentType(DocumentType.CC.name())
                .documentNumber(1234)
                .birthdate(LocalDate.of(1989, 10, 12))
                .build();
    }

    public static LeadDto createInvalidLeadDto() {
        return LeadDto.builder()
                .name("")
                .email("invalid-email")
                .phoneNumber("")
                .documentNumber(0)
                .build();
    }

    public static LeadEntity createLeadEntity() {
        return LeadEntity.builder()
                .name("John Doe")
                .email("jhon.doe@gmail.com")
                .phoneNumber("+1234567890")
                .documentNumber(1234)
                .state(LeadState.CREATED.name())
                .build();
    }

    public static LeadEntity createProspectEntity() {
        return LeadEntity.builder()
                .name("John Doe")
                .email("jhon.doe@gmail.com")
                .phoneNumber("+1234567890")
                .documentNumber(1234)
                .birthdate(LocalDate.of(1990,12,12))
                .state(LeadState.PROSPECT.name())
                .build();
    }

    public static ValidationResults createValidValidationResults() {
        return new ValidationResults();
    }

    public static ValidationResults createErrorsValidationResults(String ... errors) {
        ValidationResults results = new ValidationResults();
        Arrays.stream(errors).forEach(results::addError);
        return results;
    }

    public static ValidationOutcome createValidValidationOutcome() {
        ValidationResults results = createValidValidationResults();
        return ValidationOutcome.builder()
                .validation(results)
                .payload(null)
                .build();
    }

    public static ValidationOutcome createInvalidValidationOutcome(String ... errors) {
        ValidationResults results = createErrorsValidationResults(errors);
        return ValidationOutcome.builder()
                .validation(results)
                .payload(null)
                .build();
    }

}
