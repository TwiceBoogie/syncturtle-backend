package dev.twiceb.userservice.domain.model;

import dev.twiceb.common.enums.ActionStatus;
import dev.twiceb.common.enums.ActionType;
import jakarta.persistence.*;
import lombok.Getter;
import java.time.Instant;

@Entity
@Getter
@Table(name = "user_actions")
public class UserAction extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "action_type", nullable = false)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "action_status", nullable = false)
    private ActionStatus actionStatus;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    // UserAction -> UserDevice (multiple actions per device)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_device_id", nullable = false)
    private UserDevice userDevice;

    @Column(name = "is_user_notified")
    private boolean isUserNotified;

    @Column(name = "expiration_time")
    private Instant expirationTime;

    protected UserAction() {}; // jpa friendly

    private UserAction(ActionType type, ActionStatus status, String code, Instant expiration) {
        if (expiration == null) {
            expiration = Instant.now();
        }
        this.actionType = type;
        this.actionStatus = status;
        this.verificationCode = code;
        this.expirationTime = expiration;
        this.isUserNotified = false;
    }

    public static UserAction create(ActionType type, ActionStatus status, String code,
            Instant expiration) {
        return new UserAction(type, status, code, expiration);
    }

    public void setUserNotified() {
        this.isUserNotified = true;
    }

}
