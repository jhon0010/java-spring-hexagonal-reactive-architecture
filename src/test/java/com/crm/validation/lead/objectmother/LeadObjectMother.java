package com.crm.validation.lead.objectmother;

import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.validator.ValidationOutcome;
import com.crm.validation.lead.domain.model.validator.ValidationResults;
import com.crm.validation.lead.domain.model.enums.DocumentType;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.domain.model.valueobjects.Document;
import com.crm.validation.lead.domain.model.valueobjects.Email;
import com.crm.validation.lead.domain.model.valueobjects.LeadId;
import com.crm.validation.lead.domain.model.valueobjects.PersonalInfo;
import com.crm.validation.lead.domain.model.valueobjects.PhoneNumber;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.out.db.entities.LeadJPAEntity;

import java.time.LocalDate;
import java.util.Arrays;

public class LeadObjectMother {

    public static Lead createValidLead() {
        return Lead.builder()
                .id(LeadId.of(java.util.UUID.randomUUID()))
                .email(Email.of("peter@some.com"))
                .phoneNumber(PhoneNumber.of("+1234567890"))
                .document(Document.of(DocumentType.CC.name(), 1234))
                .personalInfo(PersonalInfo.of("Jhon Doe", LocalDate.of(1990, 12, 12)))
                .state(LeadState.CREATED)
                .build();
    }

    public static Lead createInvalidLead() {
        return Lead.builder()
                .id(LeadId.of(java.util.UUID.randomUUID()))
                .email(Email.of("peter@some.com"))
                .phoneNumber(PhoneNumber.of("+1234567890"))
                .document(Document.of(DocumentType.CC.name(), 999999)) // Invalid document number for testing
                .personalInfo(PersonalInfo.of("Jhon Doe", LocalDate.of(1990, 12, 12)))
                .state(LeadState.CREATED)
                .build();
    }

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

    public static LeadJPAEntity createLeadEntity() {
        return LeadJPAEntity.builder()
                .id(java.util.UUID.randomUUID())
                .name("John Doe")
                .email("jhon.doe@gmail.com")
                .phoneNumber("+1234567890")
                .documentType(DocumentType.CC.name())
                .documentNumber(1234)
                .birthdate(LocalDate.of(1990,12,12))
                .state(LeadState.CREATED.name())
                .build();
    }

    public static LeadJPAEntity createProspectEntity() {
        return LeadJPAEntity.builder()
                .id(java.util.UUID.randomUUID())
                .name("John Doe")
                .email("jhon.doe@gmail.com")
                .phoneNumber("+1234567890")
                .documentType(DocumentType.CC.name())
                .documentNumber(1234)
                .birthdate(LocalDate.of(1990,12,12))
                .state(LeadState.PROSPECT.name())
                .build();
    }

    public static LeadJPAEntity createRejectedEntity() {
        return LeadJPAEntity.builder()
                .id(java.util.UUID.randomUUID())
                .name("John Doe")
                .email("jhon.doe@gmail.com")
                .phoneNumber("+1234567890")
                .documentType(DocumentType.CC.name())
                .documentNumber(1234)
                .birthdate(LocalDate.of(1990,12,12))
                .state(LeadState.REJECTED.name())
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
