package com.crm.validation.lead.infrastructure.adapter.in.web;

import com.crm.validation.lead.application.ports.in.PromoteLeadUseCase;
import com.crm.validation.lead.infrastructure.adapter.in.commons.mappers.LeadWebMapper;
import com.crm.validation.lead.infrastructure.adapter.in.web.handlers.LeadRequestHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequiredArgsConstructor
@Configuration
public class LeadRouter {

    private final LeadRequestHandler leadRequestHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/leads/validate",
                    method = POST,
                    beanClass = LeadRequestHandler.class,
                    beanMethod = "validateLead",
                    operation = @Operation(
                            operationId = "validateLead",
                            summary = "Promote a lead (idempotent)",
                            description = "Idempotently changes a lead's status to PROMOTED",
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Lead data to validate and promote",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(
                                                    implementation = com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto.class
                                            ),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "Valid Lead Example",
                                                            summary = "Example of a valid lead to promote",
                                                            value = """
                                                                    {
                                                                      "name": "Jon",
                                                                      "birthdate": "1988-11-11",
                                                                      "email": "jhon23@gmail.com",
                                                                      "phoneNumber": "+310610823981",
                                                                      "documentType": "CC",
                                                                      "documentNumber": 1012112222
                                                                    }
                                                                    """
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Success"
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Lead not found"
                                    )
                            }
                    )
            )
    })
    RouterFunction<ServerResponse> leadRoutes(PromoteLeadUseCase leadValidatorService, LeadWebMapper leadWebMapper) {
        return RouterFunctions
                .route()
                .POST("/api/leads/validate", leadRequestHandler::validateLead)
                .build();
    }
}
