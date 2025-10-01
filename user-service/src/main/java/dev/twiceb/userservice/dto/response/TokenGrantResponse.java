package dev.twiceb.userservice.dto.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenGrantResponse {
    private final AccessTokenResponse at;
    private final RefreshTokenResponse rt;

    @Getter
    @AllArgsConstructor
    public static class AccessTokenResponse {
        private final String jwt;
        private final Instant exp;
    }

    @Getter
    @AllArgsConstructor
    public static class RefreshTokenResponse {
        private final String token;
        private final Instant exp;
    }
}
