package com.crm.validation.lead.infrastructure.adapter.out.endpoints;

import com.crm.validation.lead.application.ports.out.endpoints.NationalRegistryPort;
import com.crm.validation.lead.domain.model.Lead;
import com.crm.validation.lead.domain.model.validator.ValidationOutcome;
import com.crm.validation.lead.domain.model.validator.ValidationResults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class NationalRegistryAdapter extends BaseExternalCallSimulator implements NationalRegistryPort {
    public static final String SERVICE_NAME = "NationalRegistryAdapter.isPresentOnNationalRegistry";
    public static final String NATIONAL_RECORD_NOT_FOUND = "The lead is not present on the national registry: ";

    @Override
    public Mono<ValidationOutcome> apply(Lead lead) {
        log.info("Checking if lead {} is present on the national registry", lead.getDocumentNumber());

        return Mono.defer(() ->
                this.simulateNationalRegistryCheck(lead)
                .map(isPresentOnNationalRegistry -> {
                    ValidationResults result = new ValidationResults();

                    if (!isPresentOnNationalRegistry) {
                        log.warn("Lead {}, not found in the National Registry", lead.getDocumentNumber());
                        result.addError(SERVICE_NAME.concat(NATIONAL_RECORD_NOT_FOUND).concat(lead.toString()));
                    } else {
                        log.info("Lead {} successfully found in the National Registry", lead.getDocumentNumber());
                    }

                    return ValidationOutcome.builder()
                            .validation(result)
                            .payload(isPresentOnNationalRegistry)
                            .build();
                })
        );
    }
}
