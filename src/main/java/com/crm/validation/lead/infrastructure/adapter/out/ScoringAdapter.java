package com.crm.validation.lead.infrastructure.adapter.out;

import com.crm.validation.lead.application.ports.out.ScoringPort;
import com.crm.validation.lead.domain.model.Lead;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class ScoringAdapter extends BaseExternalCallSimulator implements ScoringPort {
    public static final String SERVICE_NAME = "ScoringAdapter.getScore";

    @Override
    public Mono<Double> getScore(Lead lead){
        log.info("Calculating score for lead {}", lead.getId());
        return Mono.defer(() ->
                maybeFail(SERVICE_NAME)
                        .then(simulateScoring(lead))
                        .delayElement(randomDelay())
        );
    }
}
