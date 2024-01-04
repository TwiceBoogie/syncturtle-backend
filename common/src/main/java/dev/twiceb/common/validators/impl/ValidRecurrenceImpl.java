package dev.twiceb.common.validators.impl;

import dev.twiceb.common.dto.request.NewRecurringEventRequest;
import dev.twiceb.common.validators.ValidRecurrence;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidRecurrenceImpl implements ConstraintValidator<ValidRecurrence, NewRecurringEventRequest> {
    @Override
    public void initialize(ValidRecurrence constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(NewRecurringEventRequest value, ConstraintValidatorContext context) {
        if ("custom".equals(value.getRecurrencePattern()) && value.getRecurrenceFreq() == null) {
            return false;
        }
        return true;
    }
}
