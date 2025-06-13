package com.crm.validation.lead.domain.model.enums;

public enum LeadState {
    CREATED,
    ON_VALIDATION,
    PROSPECT,
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

    /**
     * Function to compare a String with a String.
     */
    public static boolean isEqual(String state, LeadState leadState) {
        if(isValid(state)) {
            return state.equalsIgnoreCase(leadState.name());
        } else {
            return false;
        }
    }

}
