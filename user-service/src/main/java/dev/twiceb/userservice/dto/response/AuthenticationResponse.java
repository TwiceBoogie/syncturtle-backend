package dev.twiceb.userservice.dto.response;

import lombok.Data;

@Data
public class AuthenticationResponse {
    private AuthUserResponse user;
    private String token;
    private String deviceToken;
}
