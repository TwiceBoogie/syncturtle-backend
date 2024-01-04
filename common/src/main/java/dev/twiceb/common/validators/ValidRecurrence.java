package dev.twiceb.common.validators;

import dev.twiceb.common.validators.impl.ValidRecurrenceImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidRecurrenceImpl.class)
public @interface ValidRecurrence {
    String message() default "Recurrence frequency must be specified for custom pattern";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
