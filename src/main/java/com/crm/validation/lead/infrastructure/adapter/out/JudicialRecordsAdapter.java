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

    /**
     * Simulates a call to a judicial service add some delay to check if the lead has a criminal record.
     *  Sometimes returns a random exception.
     *
     *  Expected to get false in order to promote the lead to a prospect.
     *
     * @param leadDto the lead to check for a criminal record.
     * @return a Mono that emits true if the lead has a criminal record, false otherwise.
     */
    @Override
    public ValidationResult validate(LeadDto leadDto) {
        log.info("Checking if lead {} has a criminal record", leadDto.id());
        /*
        Mono.defer(() ->
             maybeFail(SERVICE_NAME)
                    .then(simulateCriminalCheck(leadDto))
                    .delayElement(randomDelay())
        );
         */

        ValidationResult result = new ValidationResult();
        if (this.simulateCriminalCheck(leadDto)) {
            result.addError(SERVICE_NAME.concat(CRIMINAL_RECORD_FOUND).concat(leadDto.name()));
        }

        return result;
    }

}
