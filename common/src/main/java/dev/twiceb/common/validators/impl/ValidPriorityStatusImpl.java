package dev.twiceb.common.validators.impl;

import dev.twiceb.common.validators.ValidPriorityStatus;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class ValidPriorityStatusImpl implements ConstraintValidator<ValidPriorityStatus, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return Arrays.asList("HIGH", "MEDIUM", "LOW", "NONE").contains(value);
    }
}
