package dev.twiceb.passwordservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.sql.Date;

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
    private PasswordExpiryPolicy passwordExpiryPolicy;

    @Column(name = "notification_sent")
    private boolean notificationSent = false;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @PrePersist
    private void prePersist() {
        LocalDate currentTime = LocalDate.now();
        if (passwordExpiryPolicy.getPolicyName().equals("default")) {
            this.expiryDate = currentTime.plusDays(90);

        } else {
            this.expiryDate = currentTime.plusDays(
                    this.passwordExpiryPolicy.getMaxExpiryDays()
            );
        }
    }
}
