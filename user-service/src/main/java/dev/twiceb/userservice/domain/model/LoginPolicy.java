package dev.twiceb.userservice.domain.model;

import io.hypersistence.utils.hibernate.type.interval.PostgreSQLIntervalType;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.Type;

import java.time.Duration;

@Entity
@Getter
@Table(name = "login_policies")
public class LoginPolicy extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_name", nullable = false, length = 50)
    private String policyName;

    @Column(name = "max_attempts", nullable = false)
    private int maxAttempts;

    @Type(PostgreSQLIntervalType.class)
    @Column(name = "lockout_duration", columnDefinition = "INTERVAL")
    private Duration lockoutDuration;

    @Type(PostgreSQLIntervalType.class)
    @Column(name = "reset_duration", columnDefinition = "INTERVAL")
    private Duration resetDuration;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;
}
