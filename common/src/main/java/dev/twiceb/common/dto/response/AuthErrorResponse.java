package dev.twiceb.common.dto.response;

import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthErrorResponse {
    private int errorCode;
    private String errorMessage;
    private Map<String, Object> payload;
}
