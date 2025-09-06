package dev.twiceb.common.dto.response;

import dev.twiceb.common.security.JwtProvider.AccessToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenGrant {
    private final AccessToken at;
    private final RefreshCookie rc;
}
