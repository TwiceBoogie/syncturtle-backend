package dev.twiceb.passwordservice.model;

import dev.twiceb.passwordservice.service.util.TransitConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "encryption_key")
public class EncryptionKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;
    // dek = data encryption key
    @Column(name = "dek")
    @Convert(converter = TransitConverter.class)
    private String dek;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "encryptionKey", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Keychain> keychains = new ArrayList<>();

    @Column(name = "algorithm")
    private String algorithm;

    @Column(name = "key_size")
    private int keySize;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "is_enabled", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isEnabled = true;

    @Column(name = "active_since", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime activeSince = LocalDateTime.now();

    @Column(name = "version", nullable = false)
    private int version = 1;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "modified_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Column(name = "usage_count")
    private int usageCount;

    @OneToOne
    @JoinColumn(name = "rotation_policy_id")
    private RotationPolicy rotationPolicy;

    public EncryptionKey() {}

    public EncryptionKey(User user, String dek, String name, String algorithm, int keySize, RotationPolicy rotationPolicy) {
        this.user = user;
        this.dek = dek;
        this.name = name;
        this.algorithm = algorithm;
        this.keySize = keySize;
        this.rotationPolicy = rotationPolicy;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime currentTime = LocalDateTime.now();
        expirationDate = currentTime.plusDays(rotationPolicy.getMaxExpiryDays());
    }

    @PreUpdate
    public void preUpdate() {
        LocalDateTime currentTime = LocalDateTime.now();
        expirationDate = currentTime.plusDays(rotationPolicy.getMaxExpiryDays());
        version = version + 1;
        modifiedAt = currentTime;
    }
}
