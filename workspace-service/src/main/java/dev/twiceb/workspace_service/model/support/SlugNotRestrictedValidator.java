package dev.twiceb.workspace_service.model.support;

import java.util.Set;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class SlugNotRestrictedValidator implements ConstraintValidator<SlugNotRestricted, String> {

    private static final Set<String> RESTRICTED =
            Set.of("api", "admin", "god-mode", "login", "signup", "settings", "auth", "static");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !RESTRICTED.contains(value.toLowerCase());
    }

}
