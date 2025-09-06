package dev.twiceb.userservice.dto.internal;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshResult {
    private final String refreshToken;
    private final Instant expiresAt; // idle version not abs
    private final String accessToken;
    private Instant accessTokenExpiration;

    // public String getRefreshToken() {
    // return refreshToken;
    // }
}
