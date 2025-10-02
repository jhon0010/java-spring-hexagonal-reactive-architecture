package com.crm.validation.lead.infrastructure.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.validation.lead.application.ports.in.PromoteLeadUseCase;
import com.crm.validation.lead.infrastructure.adapter.commons.mappers.LeadWebMapper;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadValidationResultDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Tag(name = "Leads", description = "Lead management")
@RestController
@RequestMapping("/api/leads")
public class LeadController {
    private final PromoteLeadUseCase leadValidatorService;
    private final LeadWebMapper leadWebMapper;

    @Operation(summary = "Promote a lead (idempotent)",
            description = "Idempotently changes a lead's status to PROMOTED")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Lead not found")
    })
    @PostMapping("/validate")
    public Mono<ResponseEntity<LeadValidationResultDto>> validateLead(@RequestBody LeadDto leadDto) {
        return leadValidatorService.promoteLeadToProspect(leadWebMapper.leadDtoToLead(leadDto))
                .map(leadWebMapper::leadValidationResultToDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
