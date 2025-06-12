package com.crm.validation.lead.application.ports.out;

import com.crm.validation.lead.application.services.validator.IndependentValidator;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;

public interface NationalRegistryPort extends IndependentValidator<LeadDto> {}
