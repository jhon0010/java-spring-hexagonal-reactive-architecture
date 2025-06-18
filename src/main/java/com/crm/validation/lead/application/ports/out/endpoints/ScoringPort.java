package com.crm.validation.lead.application.ports.out.endpoints;

import com.crm.validation.lead.application.services.validator.DependentValidator;
import com.crm.validation.lead.domain.model.Lead;

public interface ScoringPort extends DependentValidator<Lead> {}
