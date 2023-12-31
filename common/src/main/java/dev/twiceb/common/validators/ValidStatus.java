package dev.twiceb.common.validators;

import dev.twiceb.common.validators.impl.ValidStatusImpl;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {ValidStatusImpl.class})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidStatus {
    String message() default "Invalid Status type.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}