package dev.twiceb.userservice.model;

import dev.twiceb.common.enums.ActionStatus;
import dev.twiceb.common.enums.ActionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_actions")
public class UserAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "action_type", nullable = false)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "action_status", nullable = false)
    private ActionStatus actionStatus = ActionStatus.PENDING;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(name = "user_device_id")
    private Long userDeviceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;
}
