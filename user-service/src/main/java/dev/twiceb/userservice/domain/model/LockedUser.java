package dev.twiceb.userservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Duration;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "locked_users")
public class LockedUser extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lockout_start")
    private Instant lockoutStart;

    @Column(name = "lockout_end")
    private Instant lockoutEnd;

    // LockEvent -> User (nullable for anonymous)
    // unique handle in db else no lockout history
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = false)
    private User user;

    @Column(name = "lockout_reason", nullable = false)
    private String lockoutReason;

    @Column(name = "is_requested_by_user")
    private boolean isRequestedByUser;

    @Column(name = "failed_during_lock_count", nullable = false)
    private int failedDuringLockCount;

    @Column(name = "escalation_level", nullable = false)
    private int escalationLevel;

    @Column(name = "last_failed_attempt")
    private Instant lastFailedAttempt;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    protected LockedUser() {} // jpa friendly

    public static LockedUser lock(User user, String reason, Duration duration,
            boolean isRequested) {
        LockedUser lUser = new LockedUser();
        Instant now = Instant.now();
        lUser.lockoutStart = now;
        lUser.lockoutEnd = now.plus(duration);
        lUser.lockoutReason = reason;
        lUser.isRequestedByUser = isRequested;
        lUser.failedDuringLockCount = 0;
        lUser.escalationLevel = 0;
        lUser.version = 0L;

        return lUser;
    }

}
