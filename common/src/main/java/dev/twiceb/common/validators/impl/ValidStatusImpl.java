package dev.twiceb.common.validators.impl;

import dev.twiceb.common.validators.ValidStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ValidStatusImpl implements ConstraintValidator<ValidStatus, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Arrays.asList("TODO", "UPCOMMING", "IN-PROGRESS", "COMPLETE", "OVERDUE").contains(value);
    }
}
