package com.crm.validation.lead.infrastructure.adapter.in.web.services.validator;

import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.function.Function;

import static com.crm.validation.lead.infrastructure.adapter.in.web.services.validator.LeadDtoDataValidatorService.ValidationResult.*;

/**
 * Using the combinator pattern with functional interfaces, allowing us to chain all the functions together
 * with and function specified below.
 *
 * Combinator pattern is lazy and only run when the apply method is call.This increase the composition over inheritance principle.
 * This receives a leadDto as an argument and returns a ValidationResult that is an enum with the validations
 *
 * Functional Interfaces: Interfaces with a single abstract method, suitable for lambda expressions.
 * Combinators: Higher-order functions that combine other functions to produce new functions.
 * Function Composition: Combining multiple functions to create a new function that applies them in sequence or logical combination.
 */
@Component
public interface LeadDtoDataValidatorService extends Function<LeadDto, LeadDtoDataValidatorService.ValidationResult> {

    static LeadDtoDataValidatorService isEmailValid(){
        return leadDto -> {
            if (leadDto.email() == null || leadDto.email().isEmpty()) {
                return EMPTY_EMAIL;
            } else {
                return leadDto.email().contains("@") ? SUCCESS : EMAIL_INVALID;
            }
        };
    }

    static LeadDtoDataValidatorService isPhoneValid(){
        return leadDto -> leadDto.phoneNumber().contains("+") ? SUCCESS : PHONE_INVALID;
    }

    static LeadDtoDataValidatorService isAnAdult(){
        return leadDto -> Period.between((leadDto.birthdate()), LocalDate.now()).getYears() > 18 ?
                SUCCESS : NOT_AN_ADULT;
    }
    /**
     * Applies the validation logic to the given LeadDto.
     *
     * @param other, LeadDtoDataValidatorService to validate and compose the final chain logic.
     * @return ValidationResult indicating the result of the validation
     */
    default LeadDtoDataValidatorService and (LeadDtoDataValidatorService other) {
        return leadDto -> {
            ValidationResult result = this.apply(leadDto);
            return result.equals(SUCCESS) ? other.apply(leadDto) : result;
        };
    }

    /**
     * Enum with the possibles values for these validations.
     */
    enum ValidationResult {
        SUCCESS,
        EMAIL_INVALID,
        EMPTY_EMAIL,
        PHONE_INVALID,
        NOT_AN_ADULT,
    }

}
