package com.crm.validation.lead.infrastructure.adapter.in.web;

import com.crm.validation.lead.application.services.LeadValidatorUseCase;
import com.crm.validation.lead.domain.LeadValidationResult;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Tag(name = "Leads", description = "Lead management")
@RestController
@RequestMapping("/api/leads")
public class LeadController {
    private final LeadValidatorUseCase leadValidatorService;

    @Operation(summary = "Promote a lead (idempotent)",
            description = "Idempotently changes a lead’s status to PROMOTED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Lead not found")
    })
    @PostMapping("/validate")
    public Mono<LeadValidationResult> validateLead(@RequestBody LeadDto leadDtoMono) {
        return leadValidatorService.promoteLeadToProspect(leadDtoMono);
    }
}
