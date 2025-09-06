package dev.twiceb.userservice.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "recovery_attempts")
public class RecoveryAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RecoveryAttempt -> User
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "attempt_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime attemptTimestamp = LocalDateTime.now();

    @Column(name = "success")
    private boolean success = false;

    @Column(name = "recovery_type")
    private String recoveryType;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "is_new_device")
    private boolean isNewDevice = false;
}
