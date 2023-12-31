package dev.twiceb.common.validators;

import dev.twiceb.common.validators.impl.ValidPriorityStatusImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Constraint(validatedBy = {ValidPriorityStatusImpl.class})
@Target({FIELD, METHOD, PARAMETER, TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPriorityStatus {
    String message() default "Invalid Priority status.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
