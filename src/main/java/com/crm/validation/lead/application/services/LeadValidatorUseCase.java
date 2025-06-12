package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.out.JudicialRecordsPort;
import com.crm.validation.lead.application.ports.out.NationalRegistryPort;
import com.crm.validation.lead.application.ports.out.ScoringPort;
import com.crm.validation.lead.application.services.validator.CompositeValidator;
import com.crm.validation.lead.application.services.validator.IndependentValidator;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
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

    public LeadValidatorUseCase(JudicialRecordsPort judicialRecordsPort, NationalRegistryPort nationalRegistryPort,
                                ScoringPort scoringPort) {
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
                .map(validationOutcome -> Lead.promoteLeadToProspect(leadDto, validationOutcome.validation()));
    }

}
