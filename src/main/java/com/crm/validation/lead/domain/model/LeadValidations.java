package com.crm.validation.lead.domain.model;

import lombok.Data;

@Data
public class LeadValidations {

    private final boolean criminalRecordPresent;
    private final boolean presentOnNationalRegistry;
    private final double score;
}
