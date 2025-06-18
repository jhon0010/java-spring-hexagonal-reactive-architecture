package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.in.PromoteLeadUseCase;
import com.crm.validation.lead.application.ports.out.db.repositories.LeadRepository;
import com.crm.validation.lead.application.ports.out.endpoints.JudicialRecordsPort;
import com.crm.validation.lead.application.ports.out.endpoints.NationalRegistryPort;
import com.crm.validation.lead.application.ports.out.endpoints.ScoringPort;
import com.crm.validation.lead.application.services.validator.CompositeValidator;
import com.crm.validation.lead.application.services.validator.IndependentValidator;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.exceptions.LeadAlreadyExistException;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.LeadState;
import lombok.extern.slf4j.Slf4j;
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

    public LeadValidatorUseCase(JudicialRecordsPort judicialRecordsPort, NationalRegistryPort nationalRegistryPort,
                                ScoringPort scoringPort, LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
        this.judicialRecordsPort = judicialRecordsPort;
        this.nationalRegistryPort = nationalRegistryPort;
        this.scoringPort = scoringPort;
        this.validator = new CompositeValidator<Lead>()
                    .addIndependent(this.judicialRecordsPort)
                    .addIndependent(this.nationalRegistryPort)
                    .addDependent(this.scoringPort);
    }

    // TODO : Add more function modularity for this function.
    public Mono<LeadValidationResult> promoteLeadToProspect(Lead lead) {
        return validator.apply(lead)
                .map(validationOutcome ->
                        lead.promoteLeadToProspect(validationOutcome.validation())
                )
                .flatMap(leadValidationResult -> {
                    if (LeadState.REJECTED.equals(lead.getState())) {
                        log.warn("The lead goes rejected please see the log for further details.");
                        return this.leadRepository.save(lead)
                                .map(leadValidationResult::withLead);
                    }

                    return leadRepository.findByCoreData(lead.getEmail(), lead.getPhoneNumber(), lead.getDocument())
                        .flatMap(existingLead -> {
                            if (LeadState.PROSPECT.equals(existingLead.getState())) {
                                log.info("Lead {} is already a prospect, rejecting promotion.", existingLead.getDocumentNumber());
                                return Mono.error(new LeadAlreadyExistException(existingLead));
                            }
                            log.info("Lead {} already exists in the database, updating state to PROSPECT.",
                                    existingLead.toString());
                            // Create updated lead with the EXISTING ID from the database to ensure the update works
                            Lead updatedLead = existingLead.withState(LeadState.PROSPECT);
                            return this.leadRepository.save(updatedLead)
                                    .map(leadValidationResult::withLead);
                        })
                        .switchIfEmpty(
                            // For new leads, create a completely new entity and insert it
                            Mono.defer(() -> {
                                log.info("Lead {} not found in database, creating new lead", lead.getDocumentNumber());
                                // Create a new lead with PROSPECT state for insertion (not update)
                                Lead newLead = lead.withState(LeadState.PROSPECT);
                                return this.leadRepository.save(newLead)
                                    .map(savedLead -> {
                                        log.info("Lead {} saved as a prospect in the database with ID {}.",
                                                savedLead.getDocumentNumber(), savedLead.getId().getValue());
                                        return leadValidationResult.withLead(savedLead);
                                    });
                            })
                        );
                });
    }

}

