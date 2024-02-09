package dev.twiceb.passwordservice.model;

import dev.twiceb.common.enums.TimePeriod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "password_update_stats")
public class PasswordUpdateStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_id", nullable = false)
    private Long policyId;

    @Column(name = "avg_updates_count", nullable = false)
    private int avgUpdateCount = 0;

    @Column(name = "avg_update_interval", nullable = false)
    private Duration avgUpdateInterval;

    @Enumerated(EnumType.STRING)
    @Column(name = "interval_type", nullable = false)
    private TimePeriod intervalType;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();
}
