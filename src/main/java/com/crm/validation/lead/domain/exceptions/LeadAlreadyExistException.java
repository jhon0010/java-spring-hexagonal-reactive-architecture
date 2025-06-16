package com.crm.validation.lead.domain.exceptions;

import com.crm.validation.lead.domain.model.Lead;

public class LeadAlreadyExistException extends RuntimeException {

    public LeadAlreadyExistException(Lead lead) {
        super(String.format("Lead with document number %s already exists as a prospect", lead.getDocumentNumber()));
    }
}
