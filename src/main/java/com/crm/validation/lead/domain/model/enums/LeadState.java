package com.crm.validation.lead.domain.model.enums;

public enum LeadState {
    CREATED,
    ON_VALIDATION,
    PROSPECT,
    REFUSED, // Invalid data, could not be processed.
    REJECTED;

    /**
     * Function to validate if a String is a valid LeadState.
     */
    public static boolean isValid(String state) {
        for (LeadState leadState : LeadState.values()) {
            if (leadState.name().equalsIgnoreCase(state)) {
                return true;
            }
        }
        return false;
    }
}
