package dev.twiceb.userservice.dto.response;

import lombok.Data;

@Data
public class RegistrationEndResponse {
    private String message;
    private String deviceToken;
    private String token;
}
