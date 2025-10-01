package dev.twiceb.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResponse {
    private final String redirectionPath;
    private final TokenGrantResponse tokenGrant;
}
