package com.crm.validation.lead.domain.model;

public record LeadValidations(boolean criminalRecordPresent, boolean presentOnNationalRegistry, double score) {}