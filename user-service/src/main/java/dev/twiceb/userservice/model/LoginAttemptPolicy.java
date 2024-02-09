package dev.twiceb.userservice.model;

import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "login_attempt_policy")
public class LoginAttemptPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_name", nullable = false)
    private String policyName;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;

    @Type(PostgreSQLIntervalType.class)
    @Column(name = "lockout_duration", columnDefinition = "INTERVAL")
    private Duration lockoutDuration;

    @Type(PostgreSQLIntervalType.class)
    @Column(name = "reset_duration", columnDefinition = "INTERVAL")
    private Duration resetDuration;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate;
}
