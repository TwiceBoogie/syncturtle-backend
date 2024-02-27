package dev.twiceb.passwordservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "user_password_expiry_settings")
public class PasswordExpiryConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "keychain_id")
    private Keychain keychain;

    @OneToOne
    @JoinColumn(name = "policy_id")
    private RotationPolicy rotationPolicy;

    @Column(name = "notification_sent")
    private boolean notificationSent = false;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @PrePersist
    private void prePersist() {
        LocalDate currentTime = LocalDate.now();
        if (rotationPolicy.getPolicyName().equals("default")) {
            this.expiryDate = currentTime.plusDays(90);

        } else {
            this.expiryDate = currentTime.plusDays(
                    this.rotationPolicy.getMaxExpiryDays()
            );
        }
    }
}
