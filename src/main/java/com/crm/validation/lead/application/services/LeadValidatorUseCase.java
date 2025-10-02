package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.in.PromoteLeadUseCase;
import com.crm.validation.lead.application.ports.out.db.repositories.LeadRepository;
import com.crm.validation.lead.application.ports.out.endpoints.JudicialRecordsPort;
import com.crm.validation.lead.application.ports.out.endpoints.NationalRegistryPort;
import com.crm.validation.lead.application.ports.out.endpoints.ScoringPort;
import com.crm.validation.lead.application.services.validator.CompositeValidator;
import com.crm.validation.lead.application.services.validator.IndependentValidator;
import com.crm.validation.lead.avro.LeadPromotedAvroEvent;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.exceptions.LeadAlreadyExistException;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.kafka.producer.KafkaProducer;

import lombok.extern.slf4j.Slf4j;

import java.time.ZoneOffset;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class LeadValidatorUseCase implements PromoteLeadUseCase {

    private final JudicialRecordsPort judicialRecordsPort;
    private final NationalRegistryPort nationalRegistryPort;
    private final ScoringPort scoringPort;
    private final IndependentValidator<Lead> validator;
    private final LeadRepository leadRepository;
    private final KafkaProducer kafkaLeadPromotedProducer;

    public LeadValidatorUseCase(JudicialRecordsPort judicialRecordsPort, NationalRegistryPort nationalRegistryPort,
                                ScoringPort scoringPort, LeadRepository leadRepository,
                                KafkaProducer kafkaLeadPromotedProducer) {
        this.leadRepository = leadRepository;
        this.judicialRecordsPort = judicialRecordsPort;
        this.nationalRegistryPort = nationalRegistryPort;
        this.scoringPort = scoringPort;
        this.kafkaLeadPromotedProducer = kafkaLeadPromotedProducer;
        this.validator = new CompositeValidator<Lead>()
                    .addIndependent(this.judicialRecordsPort)
                    .addIndependent(this.nationalRegistryPort)
                    .addDependent(this.scoringPort);
    }

    /**
     * Promotes a lead to prospect after validating it.
     * This is the main orchestration method that coordinates the validation and promotion process.
     *
     * @param lead The lead to promote
     * @return A Mono containing the validation result with the updated lead
     */
    public Mono<LeadValidationResult> promoteLeadToProspect(Lead lead) {
        return validateLead(lead)
                .flatMap(this::processValidatedLead);
    }

    /**
     * Validates a lead using the composite validator.
     *
     * @param lead The lead to validate
     * @return A Mono containing the validation result
     */
    private Mono<LeadValidationResult> validateLead(Lead lead) {
        return validator.apply(lead)
                .map(validationOutcome -> lead.promoteLeadToProspect(validationOutcome.validation()));
    }

    /**
     * Processes a lead after validation, handling both rejected and approved leads.
     *
     * @param validationResult The result of the validation process
     * @return A Mono containing the final lead validation result after persistence
     */
    private Mono<LeadValidationResult> processValidatedLead(LeadValidationResult validationResult) {
        Lead lead = validationResult.lead();

        if (LeadState.REJECTED.equals(lead.getState())) {
            return handleRejectedLead(lead, validationResult);
        }

        return findAndProcessExistingLead(lead, validationResult)
                .switchIfEmpty(createNewProspectLead(lead, validationResult));
    }

    /**
     * Handles a lead that failed validation and was rejected.
     *
     * @param lead The rejected lead
     * @param validationResult The validation result
     * @return A Mono containing the updated validation result
     */
    private Mono<LeadValidationResult> handleRejectedLead(Lead lead, LeadValidationResult validationResult) {
        log.warn("The lead goes rejected please see the log for further details.");
        return this.leadRepository.save(lead)
                .map(savedLead -> {
                    // Publish domain event for rejected lead
                    kafkaLeadPromotedProducer.sendLeadToKafka(null);
                    return validationResult.withLead(savedLead);
                });
    }

    /**
     * Finds and processes an existing lead in the database.
     *
     * @param lead The lead to find and process
     * @param validationResult The validation result
     * @return A Mono containing the validation result, or empty if no existing lead found
     */
    private Mono<LeadValidationResult> findAndProcessExistingLead(Lead lead, LeadValidationResult validationResult) {
        return leadRepository.findByCoreData(lead.getEmail(), lead.getPhoneNumber(), lead.getDocument())
                .flatMap(existingLead -> {
                    if (LeadState.PROSPECT.equals(existingLead.getState())) {
                        return handleExistingProspect(existingLead);
                    }
                    return updateExistingLeadToProspect(existingLead, validationResult);
                });
    }

    /**
     * Handles the case when a lead is already a prospect.
     *
     * @param existingLead The existing lead that is already a prospect
     * @return A Mono that errors with LeadAlreadyExistException
     */
    private Mono<LeadValidationResult> handleExistingProspect(Lead existingLead) {
        log.info("Lead {} is already a prospect, rejecting promotion.", existingLead.getDocumentNumber());
        return Mono.error(new LeadAlreadyExistException(existingLead));
    }

    /**
     * Updates an existing lead to prospect state.
     *
     * @param existingLead The existing lead to update
     * @param validationResult The validation result
     * @return A Mono containing the updated validation result
     */
    private Mono<LeadValidationResult> updateExistingLeadToProspect(Lead existingLead, LeadValidationResult validationResult) {
        log.info("Lead {} already exists in the database, updating state to PROSPECT.", existingLead.toString());
        Lead updatedLead = existingLead.withState(LeadState.PROSPECT);
        return this.leadRepository.save(updatedLead)
                .map(savedLead -> {
                    // Publish domain event for lead promotion
                    kafkaLeadPromotedProducer.sendLeadToKafka(leadToAvroEvent(savedLead)); 
                    return validationResult.withLead(savedLead);
                });
    }

    private LeadPromotedAvroEvent leadToAvroEvent(Lead lead) {
        log.atInfo().log("Converting lead to Avro event: {}", lead);
        if (lead == null) {
            throw new IllegalArgumentException("Lead cannot be null");
        }

        // Create nested Avro objects
        com.crm.validation.lead.avro.LeadId avroId = com.crm.validation.lead.avro.LeadId.newBuilder()
            .setValue(lead.getId().getValue().toString())
            .build();

        com.crm.validation.lead.avro.PersonalInfo avroPersonalInfo = 
            com.crm.validation.lead.avro.PersonalInfo.newBuilder()
                .setName(lead.getPersonalInfo().name())
                .setBirthdate(lead.getPersonalInfo().birthdate().atStartOfDay().toInstant(ZoneOffset.UTC))
                .build();

        com.crm.validation.lead.avro.Email avroEmail = 
            com.crm.validation.lead.avro.Email.newBuilder()
                .setValue(lead.getEmail().getValue())
                .build();

        com.crm.validation.lead.avro.PhoneNumber avroPhoneNumber = 
            com.crm.validation.lead.avro.PhoneNumber.newBuilder()
                .setValue(lead.getPhoneNumber().getValue())
                .build();

        com.crm.validation.lead.avro.Document avroDocument = 
            com.crm.validation.lead.avro.Document.newBuilder()
                .setType(lead.getDocument().getType())
                .setNumber(lead.getDocument().getNumber()) 
                .build();

        // Build the main event
        return LeadPromotedAvroEvent.newBuilder()
            .setId(avroId)
            .setPersonalInfo(avroPersonalInfo)
            .setEmail(avroEmail)
            .setPhoneNumber(avroPhoneNumber)
            .setDocument(avroDocument)
            .setState(com.crm.validation.lead.avro.LeadState.valueOf(lead.getState().name()))
            .build();
    }

    /**
     * Creates a new prospect lead when no existing lead is found.
     *
     * @param lead The lead to create
     * @param validationResult The validation result
     * @return A Mono containing the validation result with the newly created lead
     */
    private Mono<LeadValidationResult> createNewProspectLead(Lead lead, LeadValidationResult validationResult) {
        return Mono.defer(() -> {
            log.info("Lead {} not found in database, creating new lead", lead.getDocumentNumber());
            Lead newLead = lead.withState(LeadState.PROSPECT);
            return this.leadRepository.save(newLead)
                .map(savedLead -> {
                    log.info("Lead {} saved as a prospect in the database with ID {}.",
                            savedLead.getDocumentNumber(), savedLead.getId().getValue());
                    // Publish domain event for new lead promotion
                    kafkaLeadPromotedProducer.sendLeadToKafka(leadToAvroEvent(savedLead));
                    return validationResult.withLead(savedLead);
                });
        });
    }

}
