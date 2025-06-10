package com.crm.validation.lead.application.services;

import com.crm.validation.lead.application.ports.out.JudicialRecordsPort;
import com.crm.validation.lead.application.ports.out.NationalRegistryPort;
import com.crm.validation.lead.application.ports.out.ScoringPort;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.LeadValidations;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

@AllArgsConstructor
@Service
public class LeadValidatorUseCase {

    private final JudicialRecordsPort judicialRecordsPort;
    private final NationalRegistryPort nationalRegistryPort;
    private final ScoringPort scoringPort;

    private final static int MAX_TIMEOUT_SECONDS = 5;

    public Mono<LeadValidationResult> promoteLeadToProspect(Lead lead) {

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

                    Lead leadPromoted = lead.promoteLeadToProspect(lead, validations);
                    return new LeadValidationResult(leadPromoted, validations);
                });
    }

}
