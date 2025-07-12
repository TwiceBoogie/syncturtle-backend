package dev.twiceb.common.mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.exception.AuthException;

public class FieldErrorMapper {

    private static final Map<ValidationContext, Map<String, Map<String, AuthErrorCodes>>> errorMappings =
            Map.of(ValidationContext.SIGN_UP,
                    Map.of("email",
                            Map.of("NotBlank", AuthErrorCodes.EMAIL_REQUIRED, "Email",
                                    AuthErrorCodes.INVALID_EMAIL),
                            "password",
                            Map.of("NotBlank", AuthErrorCodes.MISSING_PASSWORD, "Size",
                                    AuthErrorCodes.INVALID_PASSWORD),
                            "passwordConfirm",
                            Map.of("NotBlank", AuthErrorCodes.INVALID_PASSWORD_CONFIRM)),
                    ValidationContext.MAGIC_SIGN_UP,
                    Map.of("email",
                            Map.of("NotBlank", AuthErrorCodes.EMAIL_REQUIRED, "Email",
                                    AuthErrorCodes.INVALID_EMAIL),
                            "magicCode",
                            Map.of("NotBlank", AuthErrorCodes.INVALID_MAGIC_CODE_SIGN_UP, "Pattern",
                                    AuthErrorCodes.INVALID_MAGIC_CODE_SIGN_UP)),
                    ValidationContext.MAGIC_SIGN_IN,
                    Map.of("email",
                            Map.of("NotBlank", AuthErrorCodes.EMAIL_REQUIRED, "Email",
                                    AuthErrorCodes.INVALID_EMAIL),
                            "magicCode",
                            Map.of("NotBlank", AuthErrorCodes.INVALID_MAGIC_CODE_SIGN_IN, "Pattern",
                                    AuthErrorCodes.INVALID_MAGIC_CODE_SIGN_IN)));

    public static AuthException mapToAuthException(BindingResult result, ValidationContext ctx) {
        Map<String, String> fieldErrors = new HashMap<>();
        AuthErrorCodes selectedCode = AuthErrorCodes.GENERIC_INPUT_ERROR;

        for (FieldError error : result.getFieldErrors()) {
            String field = error.getField(); // e.g., "email"
            String constraint = error.getCode(); // e.g., "NotBlank", "Email"
            String message = error.getDefaultMessage();

            fieldErrors.put(field, message);

            // Check if we have a specific mapping
            if (selectedCode == AuthErrorCodes.GENERIC_INPUT_ERROR) {
                AuthErrorCodes mappedCode = Optional.ofNullable(errorMappings.get(ctx))
                        .map(fieldMap -> fieldMap.get(field))
                        .map(constraintMap -> constraintMap.get(constraint)).orElse(null);

                if (mappedCode != null) {
                    selectedCode = mappedCode;
                }
            }
        }

        return new AuthException(selectedCode, Map.of("errors", fieldErrors));
    }

    public enum ValidationContext {
        SIGN_UP, MAGIC_SIGN_UP, MAGIC_SIGN_IN
    }
}
