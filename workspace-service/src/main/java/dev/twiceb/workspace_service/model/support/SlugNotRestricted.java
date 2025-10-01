package dev.twiceb.workspace_service.model.support;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SlugNotRestrictedValidator.class)
public @interface SlugNotRestricted {
    String message() default "Slug is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
