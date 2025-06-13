package com.crm.validation.lead.infrastructure.adapter.out.endpoints;

import com.crm.validation.lead.application.ports.out.endpoints.NationalRegistryPort;
import com.crm.validation.lead.application.services.validator.ValidationOutcome;
import com.crm.validation.lead.application.services.validator.ValidationResults;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class NationalRegistryAdapter extends BaseExternalCallSimulator implements NationalRegistryPort {
    public static final String SERVICE_NAME = "NationalRegistryAdapter.isPresentOnNationalRegistry";
    public static final String NATIONAL_RECORD_NOT_FOUND = "The lead is not present on the national registry: ";

    @Override
    public Mono<ValidationOutcome> apply(LeadDto leadDto) {
        log.info("Checking if lead {} is present on the national registry", leadDto.documentNumber());
        ValidationResults result = new ValidationResults();

        return Mono.defer(() ->
                this.simulateNationalRegistryCheck(leadDto)
                        .filter(isPresentOnNationalRegistry -> !isPresentOnNationalRegistry)
                        .doOnNext(isPresentOnNationalRegistry -> log.warn("Lead {}, not found in the National Registry", leadDto.documentNumber()))
                        .doOnNext(isPresentOnNationalRegistry -> result.addError(SERVICE_NAME.concat(NATIONAL_RECORD_NOT_FOUND).concat(leadDto.toString())))
                        .map(isPresentOnNationalRegistry -> ValidationOutcome.builder()
                                .validation(result)
                                .payload(isPresentOnNationalRegistry)
                                .build()
                        )
        );
    }
}
