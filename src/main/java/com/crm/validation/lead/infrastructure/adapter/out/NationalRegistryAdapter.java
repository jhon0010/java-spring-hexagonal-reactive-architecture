package com.crm.validation.lead.infrastructure.adapter.out;

import com.crm.validation.lead.application.ports.out.NationalRegistryPort;
import com.crm.validation.lead.domain.model.Lead;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class NationalRegistryAdapter extends BaseExternalCallSimulator implements NationalRegistryPort {
    public static final String SERVICE_NAME = "NationalRegistryAdapter.isPresentOnNationalRegistry";

    @Override
    public Mono<Boolean> isPresentOnNationalRegistry(Lead lead) {
        log.info("Checking if lead {} is present on the national registry", lead.getId());
        return Mono.defer(() ->
                maybeFail(SERVICE_NAME)
                        .then(simulateNationalRegistryCheck(lead))
                        .delayElement(randomDelay())
        );
    }
}
