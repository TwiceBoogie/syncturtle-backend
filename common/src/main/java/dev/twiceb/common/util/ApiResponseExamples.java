package dev.twiceb.common.util;

import dev.twiceb.common.dto.response.ApiErrorResponse;

import static dev.twiceb.common.constants.ErrorMessage.*;

public class ApiResponseExamples {

    public static final String INCORRECT_USERNAME_LENGTH = "Username must be non-empty and not exceed 50 characters.";
    public static final String API_ERROR_NO_RESOURCE = "{\"message\": \"" + NO_RESOURCE_FOUND + "\", \"errors\": null}";
    public static final String API_ERROR_USERNAME_TAKEN_EXAMPLE = "{\"message\": \"" + USERNAME_ALREADY_TAKEN + "\", \"errors\": null}";
    public static final String API_ERROR_EMAIL_TAKEN_EXAMPLE = "{\"message\": \"" + EMAIL_ALREADY_TAKEN + "\", \"errors\": null}";
    public static final String API_ERROR_INPUT_ERROR = "{\"message\": \"Input Field Exception\", \"errors\": {\"field\": \"validation error\"}}";
    public static final String API_ERROR_INVALID_LOGIN = "{\"message\": \"Invalid username or password\", \"errors\": null}";
    public static final String API_ERROR_USER_NOT_FOUND = "{\"message\": \"" + USER_NOT_FOUND + "\", \"errors\": null }";
    public static final String API_ERROR_JWT = "{\"message\": \"" + JWT_TOKEN_EXPIRED + "\", \"errors\": null }";
    public static final String API_ERROR_USER_NOT_FOUND_WITH_EMAIL = "{\"message\": \"" + USER_NOT_FOUND_WITH_EMAIL + "\", \"errors\": null }";
    public static final String API_ERROR_AC_NOT_FOUND = "{\"message\": \"" + ACTIVATION_CODE_NOT_FOUND + "\", \"errors\": null }";
    public static final String API_ERROR_ACCOUNT_ALREADY_VERIFIED = "{\"message\": \"" + ACCOUNT_ALREADY_VERIFIED + "\", \"errors\": null }";
    public static final String API_ERROR_NO_DEVICE_KEY = "{\"message\": \"" + DEVICE_KEY_NOT_FOUND_OR_MATCH + "\", \"errors\": null }";
    public static final String API_ERROR_DOMAIN_EXIST = "{\"message\": \"" + DOMAIN_ALREADY_EXIST + "\", \"errors\": null }";
    public static final String API_ERROR_GEN_PASSWORD_LENGTH = "{\"message\": \"" + GENERATED_PASSWORD_LENGTH + "\", \"errors\": null }";
    public static final String API_ERROR_FILES = "{\"message\": \"No files or Too many\", \"errors\": null }";
    public static final String API_ERROR_PW_RESET_TOKEN_NOT_FOUND = "{\"message\": \"Password reset token is invalid\", \"errors\": null }";
    public static final String API_ERROR_PW_RESET_TOKEN_EXPIRED = "{\"message\": \"Reset token has expired\", \"errors\": null }";
}
