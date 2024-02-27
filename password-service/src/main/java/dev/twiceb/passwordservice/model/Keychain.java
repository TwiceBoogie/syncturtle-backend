package dev.twiceb.passwordservice.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

import dev.twiceb.passwordservice.enums.DomainStatus;
import dev.twiceb.passwordservice.service.util.DomainStatusConverter;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "keychain")
public class Keychain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "dek_id")
    private EncryptionKey encryptionKey;

    @OneToMany(mappedBy = "keychain", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PasswordChangeLog> changeLogs = new ArrayList<>();

    @OneToOne(mappedBy = "keychain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PasswordComplexityMetric complexityMetric;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rotation_policy_id")
    private RotationPolicy rotationPolicy;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(nullable = false)
    private String domain;

    @Column(name = "website_url", nullable = false)
    private String websiteUrl;

    @Column(name = "favorite", columnDefinition = "boolean default false")
    private boolean favorite = false;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "encrypted_password", columnDefinition = "bytea")
    private byte[] password;

    @Column(name = "vector", columnDefinition = "bytea")
    private byte[] vector;

    @Transient
    private String fakePassword;

    @Column(name = "status")
    @Convert(converter = DomainStatusConverter.class)
    private DomainStatus status = DomainStatus.ACTIVE;

    @Column(name = "notification_sent", columnDefinition = "boolean default false")
    private boolean notificationSent = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "keychain_categories",
            joinColumns = {@JoinColumn(name = "keychain_id")},
            inverseJoinColumns = {@JoinColumn(name = "category_id")}
    )
    private List<Category> categories = new ArrayList<>();

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    private void prePersist() {
        LocalDate currentTime = LocalDate.now();

        if ("Default".equals(rotationPolicy.getPolicyName())) {
            this.expiryDate = currentTime.plusDays(90);
        } else {
            this.expiryDate = currentTime.plusDays(this.rotationPolicy.getMaxExpiryDays());
        }
    }

    public Keychain() {
    }

    public Keychain(EncryptionKey encryptionKey, String username, String domain, String websiteUrl, byte[] password, byte[] vector) {
        this.encryptionKey = encryptionKey;
        this.username = username;
        this.domain = domain;
        this.websiteUrl = websiteUrl;
        this.password = password;
        this.vector = vector;
    }

    public String getFakePassword() {
        return "**********";
    }

}
