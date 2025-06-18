package com.crm.validation.lead.infrastructure.adapter.out.endpoints;

import com.crm.validation.lead.application.ports.out.endpoints.JudicialRecordsPort;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.validator.ValidationOutcome;
import com.crm.validation.lead.domain.model.validator.ValidationResults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class JudicialRecordsAdapter extends BaseExternalCallSimulator implements JudicialRecordsPort {
    public static final String SERVICE_NAME = "JudicialRecordsAdapter.hasCriminalRecord";
    public static final String CRIMINAL_RECORD_FOUND = "Criminal record found for lead: ";

    @Override
    public Mono<ValidationOutcome> apply(Lead lead) {
        log.info("Checking if lead {} has a criminal record", lead.getDocumentNumber());
        ValidationResults result = new ValidationResults();

        return Mono.defer(() ->
                this.simulateCriminalCheck(lead)
                        .filter(hasCriminalRecord -> hasCriminalRecord)
                        .doOnNext(hasCriminalRecord -> log.warn("Criminal record found for lead {}",
                                lead.getDocument().getType() + lead.getDocument().getNumber()))
                        .doOnNext(hasCriminalRecord -> result.addError(SERVICE_NAME.concat(CRIMINAL_RECORD_FOUND)
                                .concat(lead.toString())))
                        .map(hasCriminalRecord -> ValidationOutcome.builder()
                                .validation(result)
                                .payload(hasCriminalRecord)
                                .build()
                        )
        );
    }

}
