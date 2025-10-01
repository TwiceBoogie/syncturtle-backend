package dev.twiceb.userservice.domain.model;

import java.time.Duration;
import java.time.Instant;
import dev.twiceb.userservice.dto.internal.TokenProvenance;
import dev.twiceb.userservice.utils.TokenGenerator.TokenPair;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;

@Entity
@Table(name = "refresh_tokens")
@Getter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // bigserial
    private Long id;

    // RefreshToken -> User (many tokens per user)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "handle", nullable = false, unique = true, length = 128)
    private String handle;

    @Column(name = "secret_hash", nullable = false, length = 255)
    private String secretHash;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "absolute_expires_at", nullable = false)
    private Instant absoluteExpiresAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "last_used_at", nullable = false)
    private Instant lastUsedAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "replaced_by")
    private RefreshToken replacedBy;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    // provenance at issuance
    @Column(name = "issued_ip", length = 255)
    private String issuedIp;

    @Column(name = "issued_user_agent", length = 1024)
    private String issuedUserAgent;

    @Column(name = "issued_domain", length = 255)
    private String issuedDomain;

    @Column(name = "issued_context", length = 16)
    private String issuedContext;

    // last touch metadata
    @Column(name = "last_used_ip", length = 255)
    private String lastUsedIp;

    @Column(name = "last_used_user_agent", length = 1024)
    private String lastUsedUserAgent;

    protected RefreshToken() {} // jpa friendly

    public static RefreshToken issue(User user, TokenPair pair, TokenProvenance provenance) {
        if (user == null) {
            throw new IllegalStateException("User is required");
        }

        Instant now = provenance.getNow();

        RefreshToken rt = new RefreshToken();
        rt.user = user;
        rt.handle = pair.handle();
        // rt.secret will be set in its own method
        rt.issuedAt = now;
        rt.absoluteExpiresAt = now.plus(Duration.ofDays(90));
        rt.expiresAt = now.plus(Duration.ofDays(15));
        rt.lastUsedAt = now;
        rt.revoked = false;
        // provenance
        rt.issuedIp = provenance.getIp();
        rt.issuedUserAgent = provenance.getUserAgent();
        rt.issuedDomain = provenance.getDomain();
        rt.lastUsedIp = provenance.getIp();
        rt.issuedContext = provenance.getContext().name();

        return rt;
    }

    public RefreshToken rotate(TokenPair pair, TokenProvenance provenance) {
        // if token has been revoked; invalid;
        if (this.revoked || this.replacedBy != null) {
            throw new IllegalStateException("Token already revoked");
        }

        Instant now = provenance.getNow();

        // next rt
        RefreshToken rt = new RefreshToken();
        rt.user = this.user;
        rt.handle = pair.handle();
        // rt.secretHash will be set in its own method
        rt.issuedAt = now;
        rt.absoluteExpiresAt = this.absoluteExpiresAt; // we carry forward else we have an infinite
        rt.expiresAt = min(now.plus(Duration.ofDays(15)), this.absoluteExpiresAt);
        rt.revoked = false;

        // provenance
        rt.issuedIp = provenance.getIp();
        rt.issuedUserAgent = provenance.getUserAgent();
        rt.issuedDomain = provenance.getDomain();
        rt.lastUsedIp = provenance.getIp();
        rt.issuedContext = provenance.getContext().name();

        // revoke old one
        this.revoked = true;
        this.revokedAt = now;
        this.lastUsedAt = now;
        this.replacedBy = rt;

        return rt;
    }

    public void setSecret(String secretHash) {
        this.secretHash = secretHash;
    }

    public boolean isValid(Instant now) {
        if (now == null) {
            now = Instant.now();
        }
        return !(this.isRevoked() || now.isAfter(this.expiresAt)
                || now.isAfter(this.absoluteExpiresAt));
    }

    private static Instant min(Instant a, Instant b) {
        return a.isBefore(b) ? a : b;
    }

}
