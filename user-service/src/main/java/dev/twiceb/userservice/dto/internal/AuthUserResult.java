package dev.twiceb.userservice.dto.internal;

import dev.twiceb.common.application.internal.bundle.IssuedTokens;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthUserResult {
    private final String redirectionPath;
    private final IssuedTokens tokenGrant;
}
