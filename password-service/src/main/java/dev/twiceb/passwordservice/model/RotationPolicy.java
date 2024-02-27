package dev.twiceb.passwordservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "rotation_policies")
public class RotationPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_name", nullable = false)
    private String policyName;

    @Column(name = "max_rotation_days", nullable = false)
    private int maxExpiryDays;

    @Column(name = "notification_days", nullable = false)
    private int notificationDays;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate = LocalDateTime.now();
}
