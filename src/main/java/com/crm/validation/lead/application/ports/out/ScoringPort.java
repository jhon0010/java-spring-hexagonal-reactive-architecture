package com.crm.validation.lead.application.ports.out;

import com.crm.validation.lead.application.services.validator.DependentValidator;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;

public interface ScoringPort extends DependentValidator<LeadDto> {}
