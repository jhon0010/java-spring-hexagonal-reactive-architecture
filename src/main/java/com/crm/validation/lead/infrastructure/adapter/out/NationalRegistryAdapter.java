package com.crm.validation.lead.infrastructure.adapter.out;

import com.crm.validation.lead.application.ports.out.NationalRegistryPort;
import com.crm.validation.lead.application.services.validator.ValidationResult;
import com.crm.validation.lead.domain.model.Lead;
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
    public ValidationResult validate(LeadDto leadDto) {
        log.info("Checking if lead {} is present on the national registry", leadDto.id());

        /*
        return Mono.defer(() ->
                maybeFail(SERVICE_NAME)
                        .then(simulateNationalRegistryCheck(lead))
                        .delayElement(randomDelay())
        );

         */

        ValidationResult result = new ValidationResult();
        if (!this.simulateNationalRegistryCheck(leadDto)) {
            result.addError(SERVICE_NAME.concat(NATIONAL_RECORD_NOT_FOUND).concat(leadDto.toString()));
        }

        return result;

    }
}
