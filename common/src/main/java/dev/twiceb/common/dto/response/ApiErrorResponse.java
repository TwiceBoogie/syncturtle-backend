package dev.twiceb.common.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class ApiErrorResponse {
    private String message;
    private Map<String, String> errors;
}
