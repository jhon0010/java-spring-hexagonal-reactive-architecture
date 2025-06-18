package com.crm.validation.lead.domain.model;

import com.crm.validation.lead.application.services.validator.ValidationResults;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.domain.model.valueobjects.Email;
import com.crm.validation.lead.domain.model.valueobjects.Document;
import com.crm.validation.lead.domain.model.valueobjects.LeadId;
import com.crm.validation.lead.domain.model.valueobjects.PersonalInfo;
import com.crm.validation.lead.domain.model.valueobjects.PhoneNumber;
import lombok.Builder;
import lombok.Value;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Lead domain model representing a potential customer.
 * This class is immutable by design, as enforced by @Value.
 * It uses Value Objects to encapsulate related attributes and behaviors.
 */
@Log4j2
@Value
public class Lead {
    LeadId id;
    PersonalInfo personalInfo;
    Email email;
    PhoneNumber phoneNumber;
    Document document;
    LeadState state;

    private static final double SCORE_THRESHOLD = 60;

    @Builder
    private Lead(LeadId id, PersonalInfo personalInfo, Email email, PhoneNumber phoneNumber,
                Document document, LeadState state) {
        this.id = id;
        this.personalInfo = personalInfo;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.document = document;
        this.state = state;
    }

    /**
     * Static factory method to create a Lead with validated value objects
     */
    public static Lead create(UUID id, String name, LocalDate birthdate, String email,
                              String phoneNumber, String documentType, int documentNumber, LeadState state) {
        return Lead.builder()
                .id(id != null ? LeadId.of(id) : LeadId.generate())
                .personalInfo(PersonalInfo.of(name, birthdate))
                .email(Email.of(email))
                .phoneNumber(PhoneNumber.of(phoneNumber))
                .document(Document.of(documentType, documentNumber))
                .state(state)
                .build();
    }

    public LeadValidationResult promoteLeadToProspect(ValidationResults result) {

        log.info("Starting validations in order to promote a lead {} to {}", this, LeadState.PROSPECT);
        if (!result.isValid()) {
            log.warn("Validation failed, the prospect will be rejected:");
            result.getErrors().forEach(err -> log.error("  • {}", err));
            return LeadValidationResult
                    .builder()
                    .lead(this.withState(LeadState.REJECTED))
                    .validations(result)
                    .build();
        }
        log.info("CONGRATULATIONS !!! , The lead {} will be PROMOTED to {}",
                this.getDocument().getNumber(), LeadState.PROSPECT);
        return LeadValidationResult
                .builder()
                .lead(this.withState(LeadState.PROSPECT))
                .validations(result)
                .build();
    }

    public boolean isScoreEnoughToPromoteToProspect(double score) {
        return score >= SCORE_THRESHOLD;
    }

    /**
     * Returns a new Lead with the same data but a different state
     */
    public Lead withState(LeadState newState) {
        return Lead.builder()
                .id(this.id)
                .personalInfo(this.personalInfo)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .document(this.document)
                .state(newState)
                .build();
    }

    public Lead withOutId() {
        return Lead.builder()
                .personalInfo(this.personalInfo)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .document(this.document)
                .state(this.state)
                .build();
    }

    /**
     * Checks if the lead is an adult
     */
    public boolean isAdult() {
        return personalInfo.isAdult();
    }

    /**
     * Gets the name of the lead
     */
    public String getName() {
        return personalInfo.name();
    }

    /**
     * Gets the birthdate of the lead
     */
    public LocalDate getBirthdate() {
        return personalInfo.birthdate();
    }

    /**
     * Gets the document number of the lead
     */
    public int getDocumentNumber() {
        return document.getNumber();
    }

    /**
     * Gets the document type of the lead
     */
    public String getDocumentType() {
        return document.getType();
    }

    /**
     * Gets the string representation of the lead ID
     */
    public UUID getIdValue() {
        return id.getValue();
    }
}
