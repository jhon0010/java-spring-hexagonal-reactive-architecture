package com.crm.validation.lead.infrastructure.adapter.out;

import com.crm.validation.lead.application.ports.out.JudicialRecordsPort;
import com.crm.validation.lead.application.services.validator.ValidationResult;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class JudicialRecordsAdapter extends BaseExternalCallSimulator implements JudicialRecordsPort {
    public static final String SERVICE_NAME = "JudicialRecordsAdapter.hasCriminalRecord";
    public static final String CRIMINAL_RECORD_FOUND = "Criminal record found for lead: ";

    @Override
    public Mono<ValidationResult> validate(LeadDto leadDto) {
        log.info("Checking if lead {} has a criminal record", leadDto.documentNumber());
        ValidationResult result = new ValidationResult();

        return Mono.defer(() ->
                this.simulateCriminalCheck(leadDto)
                        .filter(hasCriminalRecord -> hasCriminalRecord)
                        .doOnNext(hasCriminalRecord -> log.warn("Criminal record found for lead {}", leadDto.id()))
                        .doOnNext(hasCriminalRecord -> result.addError(SERVICE_NAME.concat(CRIMINAL_RECORD_FOUND).concat(leadDto.toString())))
                        .map(x -> result)
        );
    }

}
