package com.crm.validation.lead.infrastructure.adapter.out.endpoints;

import com.crm.validation.lead.application.ports.out.endpoints.ScoringPort;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.validator.ValidationOutcome;
import com.crm.validation.lead.domain.model.validator.ValidationResults;
import com.crm.validation.lead.domain.exceptions.IndependentValidationFailsException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;


@Log4j2
@Component
public class ScoringAdapter extends BaseExternalCallSimulator implements ScoringPort {
    public static final String SERVICE_NAME = "ScoringAdapter.getScore";
    public static final String SCORE_NOT_ENOUGH = "The lead score is not enough to promote to prospect: ";

    @Override
    public Mono<ValidationOutcome> apply(Lead lead, ValidationOutcome... previous) throws IndependentValidationFailsException {
        log.info("Calculating score for lead {}", lead.toString());

        return Mono.defer(() ->
                this.simulateScoring(lead, previous)
                        .map(lead::isScoreEnoughToPromoteToProspect)
                        .map(isScoreEnough -> {
                            ValidationResults result = new ValidationResults();

                            if (!isScoreEnough) {
                                log.warn("Lead {}, doesn't get enough score points to be promoted", lead.getDocumentNumber());
                                result.addError(SERVICE_NAME.concat(SCORE_NOT_ENOUGH).concat(lead.toString()));
                            } else {
                                log.info("Lead {} has sufficient score for promotion", lead.getDocumentNumber());
                            }

                            return ValidationOutcome.builder()
                                    .validation(result)
                                    .payload(isScoreEnough)
                                    .build();
                        })
        );
    }
}
