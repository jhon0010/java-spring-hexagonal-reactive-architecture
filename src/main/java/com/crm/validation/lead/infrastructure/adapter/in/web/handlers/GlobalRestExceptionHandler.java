package com.crm.validation.lead.infrastructure.adapter.in.web.handlers;

import com.crm.validation.lead.domain.exceptions.IndependentValidationFailsException;
import com.crm.validation.lead.domain.exceptions.InvalidLeadDataException;
import com.crm.validation.lead.domain.exceptions.LeadAlreadyExistException;
import com.crm.validation.lead.infrastructure.adapter.in.web.envelopes.ApiError;
import com.crm.validation.lead.infrastructure.adapter.in.web.envelopes.ApiResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Log4j2
@RestControllerAdvice
public class GlobalRestExceptionHandler {

    @ExceptionHandler(IndependentValidationFailsException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDuplicate(
            IndependentValidationFailsException ex, ServerWebExchange exchange) {

        ApiError err = ApiError.of("LEAD_EXISTS", ex.getMessage());
        return wrap(err);
    }

    @ExceptionHandler(InvalidLeadDataException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidation(
            InvalidLeadDataException ex, ServerWebExchange exchange) {

        ApiError err = new ApiError("LEAD_DATA_VALIDATION_ERROR", ex.getMessage(),
                List.of("Ensure the lead data is valid"));
        return wrap(err);
    }

    @ExceptionHandler(LeadAlreadyExistException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidation(
            LeadAlreadyExistException ex, ServerWebExchange exchange) {

        ApiError err = new ApiError("LEAD_ALREADY_EXIST", ex.getMessage(),
                List.of("The lead should be unique in the system , already processed as a PROSPECT"));
        return wrap(err);
    }

    /**
     *  --------- fallback for every other error ---------
     */
    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGeneric(
            Throwable ex, ServerWebExchange exchange) {

        ApiError err = ApiError.of("INTERNAL_ERROR", "Unexpected server error");
        log.error("Unhandled exception occurred: {}", ex.getMessage(), ex);
        return wrap(err);
    }

    private Mono<ResponseEntity<ApiResponse<Void>>> wrap(ApiError err) {
        return Mono.just(ResponseEntity.ok(ApiResponse.fail(err)));
    }

}
