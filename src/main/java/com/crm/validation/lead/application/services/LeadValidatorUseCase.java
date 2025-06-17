package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.out.endpoints.JudicialRecordsPort;
import com.crm.validation.lead.application.ports.out.endpoints.NationalRegistryPort;
import com.crm.validation.lead.application.ports.out.endpoints.ScoringPort;
import com.crm.validation.lead.application.services.validator.CompositeValidator;
import com.crm.validation.lead.application.services.validator.IndependentValidator;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.exceptions.LeadAlreadyExistException;
import com.crm.validation.lead.domain.mappers.LeadMapper;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.enums.LeadState;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.out.db.repositories.LeadRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class LeadValidatorUseCase {

    private final JudicialRecordsPort judicialRecordsPort;
    private final NationalRegistryPort nationalRegistryPort;
    private final ScoringPort scoringPort;
    private final IndependentValidator<LeadDto> validator;
    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper = LeadMapper.INSTANCE;

    public LeadValidatorUseCase(JudicialRecordsPort judicialRecordsPort, NationalRegistryPort nationalRegistryPort,
                                ScoringPort scoringPort, LeadRepository leadRepository) {
        this.leadRepository = leadRepository;
        this.judicialRecordsPort = judicialRecordsPort;
        this.nationalRegistryPort = nationalRegistryPort;
        this.scoringPort = scoringPort;
        this.validator = new CompositeValidator<LeadDto>()
                    .addIndependent(this.judicialRecordsPort)
                    .addIndependent(this.nationalRegistryPort)
                    .addDependent(this.scoringPort);
    }

    public Mono<LeadValidationResult> promoteLeadToProspect(LeadDto leadDto) {
        return validator.apply(leadDto)
                .map(validationOutcome ->
                        Lead.promoteLeadToProspect(leadDto, validationOutcome.validation())
                )
                .flatMap(leadValidationResult -> {

                    Lead lead = leadValidationResult.getLead();

                    if (LeadState.REJECTED.equals(lead.getState())) {
                        log.warn("The lead goes rejected please see the log for further details.");
                        return this.leadRepository.save(leadMapper.leadToLeadEntity(lead))
                                .map(savedEntity -> {
                                    leadValidationResult.withLead(leadMapper.leadEntityToLead(savedEntity));
                                    return leadValidationResult;
                                });
                    }

                        return leadRepository.findByCoreData(lead.getEmail(), lead.getPhoneNumber(), lead.getDocumentNumber())
                            .flatMap(entity -> {
                                if (LeadState.isEqual(entity.getState(),LeadState.PROSPECT)){
                                    log.info("Lead {} is already a prospect, rejecting promotion.", lead.getDocumentNumber());
                                    return Mono.error(new LeadAlreadyExistException(lead));
                                }
                                log.info("Lead {} already exists in the database, updating state to PROSPECT.", lead.getDocumentNumber());
                                Lead updatedLead = LeadMapper.changeState(lead, LeadState.PROSPECT);
                                return this.leadRepository.save(leadMapper.leadToLeadEntity(updatedLead))
                                        .map(savedEntity -> {
                                            leadValidationResult.withLead(leadMapper.leadEntityToLead(savedEntity));
                                            return leadValidationResult;
                                        });
                            })
                            .switchIfEmpty(
                                this.leadRepository.save(leadMapper.leadToLeadEntity(leadValidationResult.getLead()))
                                    .map(savedLead -> {
                                        log.info("Lead {} saved as a prospect in the database.", savedLead.getDocumentNumber());
                                        leadValidationResult.withLead(leadMapper.leadEntityToLead(savedLead));
                                        return leadValidationResult;
                                    })
                            );
                });
    }

}

