package com.crm.validation.lead.domain.model.validator;

import com.crm.validation.lead.infrastructure.adapter.in.web.dtos.LeadDto;

import java.time.LocalDate;
import java.time.Period;
import java.util.function.Function;

import static com.crm.validation.lead.domain.model.validator.LeadDataValidatorService.ValidationResult.*;

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
public interface LeadDataValidatorService extends Function<LeadDto, LeadDataValidatorService.ValidationResult> {

    static LeadDataValidatorService isEmailValid(){
        return lead -> {
            if (lead.email() == null || lead.email().isEmpty()) {
                return EMPTY_EMAIL;
            } else {
                return lead.email().contains("@") ? SUCCESS : EMAIL_INVALID;
            }
        };
    }

    static LeadDataValidatorService isPhoneValid(){
        return lead -> lead.phoneNumber().contains("+") ? SUCCESS : PHONE_INVALID;
    }

    static LeadDataValidatorService isAnAdult(){
        return lead -> Period.between((lead.birthdate()), LocalDate.now()).getYears() > 18 ?
                SUCCESS : NOT_AN_ADULT;
    }

    /**
     * This is the method where the magic happen allowing us to
     * call an UserValidatorService function then with the 'and' function here
     * we can chain the others functions to have custom validations with specific
     * functions.
     * @param other One of the functions defined in this functional interface.
     * @return UserValidatorService -> ValidationResult
     */
    default LeadDataValidatorService and (LeadDataValidatorService other) {
        return user -> {
            ValidationResult result = this.apply(user);
            return result.equals(SUCCESS) ? other.apply(user) : result;
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
