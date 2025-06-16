package com.crm.validation.lead.application.ports.mocks;

import com.crm.validation.lead.application.ports.out.endpoints.ScoringPort;
import com.crm.validation.lead.application.services.validator.ValidationOutcome;
import com.crm.validation.lead.application.services.validator.ValidationResults;
import com.crm.validation.lead.domain.exceptions.IndependentValidationFailsException;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.objectmother.LeadObjectMother;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Log4j2
public class ScoringMock implements ScoringPort {

    private boolean shouldPass;

    public ScoringMock(boolean shouldPass) {
        this.shouldPass = shouldPass;
    }

    @Override
    public Mono<ValidationOutcome> apply(LeadDto leadDto, ValidationOutcome ... previousResults) throws IndependentValidationFailsException {

        Arrays.stream(previousResults).forEach(prev -> {
           if (!prev.validation().isValid()) {
               log.error("Previous validation failed: {} at ScoringMock", prev.validation().getErrors());
               shouldPass = false;
           }
        });

        if (shouldPass) {
            return Mono.just(LeadObjectMother.createValidValidationOutcome());
        } else {
            return Mono.just(LeadObjectMother.createInvalidValidationOutcome("The internal lead score was not enough to promote to PROSPECT"));
        }
    }
}
