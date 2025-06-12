package com.crm.validation.lead.infrastructure.adapter.out;

import com.crm.validation.lead.application.ports.out.ScoringPort;
import com.crm.validation.lead.application.services.validator.ValidationResult;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class ScoringAdapter extends BaseExternalCallSimulator implements ScoringPort {
    public static final String SERVICE_NAME = "ScoringAdapter.getScore";
    public static final String SCORE_NOT_ENOUGH = "The lead score is not enough to promote to prospect: ";

    @Override
    public Mono<ValidationResult> validate(LeadDto leadDto){
        log.info("Calculating score for lead {}", leadDto.toString());
        ValidationResult result = new ValidationResult();

        return Mono.defer(() ->
                this.simulateScoring(leadDto)
                        .map(Lead::isScoreEnoughToPromoteToProspect)
                        .filter(isScoreEnough -> !isScoreEnough)
                        .doOnNext(isScoreEnough -> log.warn("Lead {}, doesn't get enough score point to be promoted", leadDto.documentNumber()))
                        .doOnNext(isScoreEnough -> result.addError(SERVICE_NAME.concat(SCORE_NOT_ENOUGH).concat(leadDto.toString())))
                        .map(x -> result)
        );
    }
}
