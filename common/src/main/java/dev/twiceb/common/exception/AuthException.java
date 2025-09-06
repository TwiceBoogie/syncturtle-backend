package dev.twiceb.common.exception;

import java.util.HashMap;
import java.util.Map;

import dev.twiceb.common.dto.response.AuthErrorResponse;
import dev.twiceb.common.enums.AuthErrorCodes;

public class AuthException extends RuntimeException {
    private final AuthErrorCodes errorCode;
    private final Map<String, Object> payload;

    public AuthException(AuthErrorCodes errorCodes) {
        this(errorCodes, new HashMap<>());
    }

    public AuthException(AuthErrorCodes errorCode, Map<String, Object> payload) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.payload = payload != null ? payload : new HashMap<>();
    }

    public AuthErrorResponse toErrorResponse() {
        AuthErrorResponse response = new AuthErrorResponse();
        response.setErrorCode(errorCode.getCode());
        response.setErrorMessage(errorCode.getMessage());
        response.setPayload(payload);
        return response;
    }

    public Map<String, String> getMapVersion() {
        return Map.of("error_code", String.valueOf(errorCode.getCode()), "error_message",
                errorCode.getMessage());
    }
}
