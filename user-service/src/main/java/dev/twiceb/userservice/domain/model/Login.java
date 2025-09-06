package dev.twiceb.userservice.domain.model;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.userservice.domain.enums.LoginContext;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Getter
@Table(name = "logins")
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    // LoginAttempt -> User (nullable for anonymous)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // fk created by liquibase
    private User user;

    @Column(name = "attempt_timestamp", nullable = false)
    private Instant attemptTimestamp;

    @Column(name = "success", nullable = false)
    private boolean success;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "ip_address", length = 255)
    private String ipAddress;

    @Column(name = "user_agent", length = 1024)
    private String userAgent;

    @Column(name = "domain", length = 255)
    private String domain;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_medium", length = 32, nullable = false)
    private AuthMedium authMedium;

    @Enumerated(EnumType.STRING)
    @Column(name = "context", length = 16, nullable = false)
    private LoginContext context;

    @Column(name = "device_id")
    private UUID deviceId;

    @Column(name = "is_new_device")
    private boolean isNewDevice;

    @Column(name = "request_id", length = 64)
    private String requestId;

    @Column(name = "correlation_id", length = 64)
    private String correlationId;

    @Column(name = "jwt_jti", length = 64)
    private String jwtJti;

    @Column(name = "refresh_token_id")
    private Long refreshTokenId; // fk to refresh_tokens.id (BIGINT) via liqui

    protected Login() {} // jpa-friendly

    public static Login failure(User user, String failureReason, RequestMetadata meta,
            AuthMedium medium, LoginContext context, boolean isNewDevice, Long refreshTokenId) {
        return new Login(user, false, failureReason, meta.getIpAddress(), meta.getUserAgent(),
                meta.getDomain(), medium, context, null, isNewDevice, meta.getRequestId(),
                meta.getCorrelationId(), null, refreshTokenId, Instant.now());
    }

    public static Login success(User user, RequestMetadata meta, AuthMedium medium,
            LoginContext context, boolean isNewDevice) {
        return new Login(user, true, null, meta.getIpAddress(), meta.getUserAgent(),
                meta.getDomain(), medium, context, null, isNewDevice, meta.getRequestId(),
                meta.getCorrelationId(), null, null, Instant.now());
    }

    private Login(User user, boolean success, String failureReason, String ipAddress,
            String userAgent, String domain, AuthMedium medium, LoginContext context, UUID deviceId,
            boolean isNewDevice, String requestId, String correlationId, String jwtJti,
            Long refreshTokenId, Instant attemptTimestamp) {
        this.user = user;
        this.success = success;
        this.failureReason = failureReason;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.domain = domain;
        this.authMedium = medium;
        this.context = context;
        this.deviceId = deviceId;
        this.isNewDevice = isNewDevice;
        this.requestId = requestId;
        this.correlationId = correlationId;
        this.jwtJti = jwtJti;
        this.refreshTokenId = refreshTokenId;
        this.attemptTimestamp = attemptTimestamp != null ? attemptTimestamp : Instant.now();
    }
}
