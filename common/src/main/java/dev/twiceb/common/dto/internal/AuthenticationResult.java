package dev.twiceb.common.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthenticationResult {
    private final AuthUserResult user;
}
