package com.crm.validation.lead.infrastructure.adapter.out;

import com.crm.validation.lead.application.ports.out.JudicialRecordsPort;
import com.crm.validation.lead.domain.model.Lead;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
public class JudicialRecordsAdapter extends BaseExternalCallSimulator implements JudicialRecordsPort {
    public static final String SERVICE_NAME = "JudicialRecordsAdapter.hasCriminalRecord";

    /**
     * Simulates a call to a judicial service add some delay to check if the lead has a criminal record.
     *  Sometimes returns a random exception.
     *
     *  Expected to get false in order to promote the lead to a prospect.
     *
     * @param lead the lead to check for a criminal record.
     * @return a Mono that emits true if the lead has a criminal record, false otherwise.
     */
    @Override
    public Mono<Boolean> hasCriminalRecord(Lead lead) {
        log.info("Checking if lead {} has a criminal record", lead.getId());
        return Mono.defer(() ->
             maybeFail(SERVICE_NAME)
                    .then(simulateCriminalCheck(lead))
                    .delayElement(randomDelay())
        );
    }

}
