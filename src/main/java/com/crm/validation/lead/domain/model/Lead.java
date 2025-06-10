package com.crm.validation.lead.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;

@Log4j2
@Builder
@Data
public class Lead {
    private final String id;
    private final String name;
    private final LocalDate birthdate;
    private final LeadState state;

    private static final double SCORE_THRESHOLD = 60;

    public Lead promoteLeadToProspect(Lead lead, LeadValidations validations) {

        if (!validations.isCriminalRecordPresent()
                && validations.isPresentOnNationalRegistry()
                && this.isScoreEnoughToPromoteToProspect(validations.getScore())
        ) {
            return Lead.builder()
                    .id(lead.id)
                    .name(lead.name)
                    .state(LeadState.PROSPECT)
                    .birthdate(lead.birthdate)
                    .build();
        }
        log.info("The lead can not be promoted to {}", LeadState.PROSPECT);
        return lead;
    }

    public boolean isScoreEnoughToPromoteToProspect(double score){
        return score >= SCORE_THRESHOLD;
    }

}
