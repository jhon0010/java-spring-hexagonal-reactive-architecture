package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.out.JudicialRecordsPort;
import com.crm.validation.lead.application.ports.out.NationalRegistryPort;
import com.crm.validation.lead.application.ports.out.ScoringPort;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.LeadValidations;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@AllArgsConstructor
@Service
public class LeadValidatorUseCase {

    private final JudicialRecordsPort judicialRecordsPort;
    private final NationalRegistryPort nationalRegistryPort;
    private final ScoringPort scoringPort;

    private final static int MAX_TIMEOUT_SECONDS = 5;

    public Mono<LeadValidationResult> promoteLeadToProspect(Lead lead) {

        log.info("Validating lead with id: {}", lead.getId());
        LeadDto leadDto = LeadDto.builder()
                .id(lead.getId())
                .email(lead.getEmail())
                .phoneNumber(lead.getPhoneNumber())
                .birthdate(lead.getBirthdate())
                .build();

        Mono<Boolean> hasCriminalRecord = judicialRecordsPort.hasCriminalRecord(lead)
                .timeout(Duration.ofSeconds(MAX_TIMEOUT_SECONDS))
                .onErrorReturn(true);

        Mono<Boolean> isPresentOnNationalRegistry = nationalRegistryPort.isPresentOnNationalRegistry(lead)
                .timeout(Duration.ofSeconds(MAX_TIMEOUT_SECONDS))
                .onErrorReturn(false);

        return Mono.zip(
                hasCriminalRecord,
                isPresentOnNationalRegistry,
                scoringPort.getScore(lead)
        )
                .map(tuple -> {
                    boolean criminalRecordPresent = tuple.getT1();
                    boolean presentOnNationalRegistry = tuple.getT2();
                    double score = tuple.getT3();

                    LeadValidations validations = new LeadValidations(
                            criminalRecordPresent,
                            presentOnNationalRegistry,
                            score
                    );
                    log.info("Lead with id {} got the following validations: {}", lead.getId(), validations);
                    Lead leadPromoted = lead.promoteLeadToProspect(lead, validations);
                    return new LeadValidationResult(leadPromoted, validations);
                });
    }

}
