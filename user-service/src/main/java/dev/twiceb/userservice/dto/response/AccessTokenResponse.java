package dev.twiceb.userservice.dto.response;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessTokenResponse {
    String accessToken;
    Instant expiresAt;
}
