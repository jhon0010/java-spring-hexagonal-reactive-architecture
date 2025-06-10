package com.crm.validation.lead.infrastructure.adapter.in.web;

import com.crm.validation.lead.application.services.LeadValidatorUseCase;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.in.web.mappers.LeadMapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api/leads")
public class LeadController {
    private final LeadValidatorUseCase leadValidatorService;

    @PostMapping("/validate")
    public Mono<LeadValidationResult> validateLead(@RequestBody Mono<LeadDto> leadDtoMono) {
        return leadDtoMono
                .map(dto -> LeadMapper.dtoToDomain(dto))
                .flatMap(leadValidatorService::promoteLeadToProspect);
    }
}
