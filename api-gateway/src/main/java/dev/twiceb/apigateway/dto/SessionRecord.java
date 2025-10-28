package dev.twiceb.apigateway.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder(toBuilder = true)
public class SessionRecord {
    UUID userId;
    List<String> roles;
    Instant issuedAt;
    Instant expiresAt;
    Instant absoluteExpiresAt;
    boolean revoked;
}
