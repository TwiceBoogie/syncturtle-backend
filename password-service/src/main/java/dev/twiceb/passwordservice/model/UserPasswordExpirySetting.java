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
public class UserPasswordExpirySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "keychain_id")
    private Keychain keychain;

    @OneToOne
    @JoinColumn(name = "policy_id")
    private PasswordExpiryPolicy passwordExpiryPolicy;

    @Column(name = "expiry_notification_sent")
    private boolean expiryNotificationSent = false;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;

    @PrePersist
    private void prePersist() {
        LocalDate currentTime = LocalDate.now();
        if (passwordExpiryPolicy.getPolicyName().equals("default")) {
            LocalDate date90DaysFromNow = currentTime.plusDays(90);
            this.expiryDate = Date.valueOf(date90DaysFromNow);
        } else {
            LocalDate expiryPolicyMaxExpiryDaysFromNow = currentTime.plusDays(
                    this.passwordExpiryPolicy.getMaxExpiryDays()
            );
            this.expiryDate = Date.valueOf(expiryPolicyMaxExpiryDaysFromNow);
        }
    }
}
