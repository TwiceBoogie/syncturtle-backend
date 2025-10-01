package dev.twiceb.common.application.internal.bundle;

import dev.twiceb.common.application.value.AccessToken;
import dev.twiceb.common.application.value.RefreshTokenValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class IssuedTokens {
    private final AccessToken at;
    private final RefreshTokenValue rc;
}
