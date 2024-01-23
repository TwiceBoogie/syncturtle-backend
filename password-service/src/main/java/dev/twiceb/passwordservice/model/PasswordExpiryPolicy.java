package dev.twiceb.passwordservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "password_expiry_policies")
public class PasswordExpiryPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "policy_name", nullable = false)
    private String policyName;

    @Column(name = "max_expiry_days", nullable = false)
    private int maxExpiryDays;

    @Column(name = "notification_days", nullable = false)
    private int notificationDays;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @PrePersist
    private void prePersist() {
        this.createdDate = new Timestamp(System.currentTimeMillis());
    }
}
