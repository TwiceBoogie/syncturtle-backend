package dev.twiceb.userservice.dto.internal;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import dev.twiceb.userservice.domain.enums.LoginContext;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TokenProvenance {
    String ip; // issued_ip
    String userAgent; // issued_user_agent
    String domain; // issued_domain (app/admin/etc hostname)
    LoginContext context;
    UUID deviceId;
    String requestId;
    String correlationId; // for cross-service tracing
    Instant now;

    public Optional<UUID> deviceIdOpt() {
        return Optional.ofNullable(deviceId);
    }
}
