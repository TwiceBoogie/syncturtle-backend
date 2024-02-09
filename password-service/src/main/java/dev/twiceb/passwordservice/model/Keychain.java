package dev.twiceb.passwordservice.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;

import dev.twiceb.passwordservice.enums.DomainStatus;
import dev.twiceb.passwordservice.service.util.DomainStatusConverter;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "keychain", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "account_id", "domain" })
})
public class Keychain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Accounts account;

    @OneToOne(mappedBy = "keychain", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EncryptionKey encryptionKey;

    @OneToMany(mappedBy = "keychain", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PasswordChangeLog> changeLogs = new ArrayList<>();

    @OneToOne(mappedBy = "keychain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PasswordComplexityMetric complexityMetric;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private PasswordExpiryPolicy policy;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(nullable = false)
    private String domain;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "encrypted_password", columnDefinition = "bytea")
    private byte[] password;

    @Transient
    private String fakePassword;

    @Column(name = "status")
    @Convert(converter = DomainStatusConverter.class)
    private DomainStatus status = DomainStatus.ACTIVE;

    @Column(name = "notification_sent", columnDefinition = "boolean default false")
    private boolean notificationSent = false;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    private void prePersist() {
        LocalDate currentTime = LocalDate.now();

        if ("Default".equals(policy.getPolicyName())) {
            this.expiryDate = currentTime.plusDays(90);
        } else {
            this.expiryDate = currentTime.plusDays(this.policy.getMaxExpiryDays());
        }
    }

    public Keychain() {
    }

    public Keychain(Accounts account, EncryptionKey encryptionKey, String username, String domain, byte[] password) {
        this.account = account;
        this.encryptionKey = encryptionKey;
        this.username = username;
        this.domain = domain;
        this.password = password;

        encryptionKey.setKeychain(this);
    }

    public String getFakePassword() {
        return "**********";
    }

}
