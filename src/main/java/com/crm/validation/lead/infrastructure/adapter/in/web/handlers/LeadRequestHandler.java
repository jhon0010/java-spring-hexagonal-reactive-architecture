package com.crm.validation.lead.infrastructure.adapter.in.web.handlers;

import com.crm.validation.lead.application.ports.in.PromoteLeadUseCase;
import com.crm.validation.lead.infrastructure.adapter.in.commons.mappers.LeadWebMapper;
import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LeadRequestHandler {

    private final PromoteLeadUseCase leadValidatorService;
    private final LeadWebMapper leadWebMapper;

    public Mono<ServerResponse> validateLead(ServerRequest request) {
        return request.bodyToMono(LeadDto.class)
                .flatMap(leadDto -> leadValidatorService.promoteLeadToProspect(leadWebMapper.leadDtoToLead(leadDto))
                        .map(leadWebMapper::leadValidationResultToDto)
                        .flatMap(result -> ServerResponse.ok().bodyValue(result))
                        .switchIfEmpty(ServerResponse.notFound().build()));
    }

}
