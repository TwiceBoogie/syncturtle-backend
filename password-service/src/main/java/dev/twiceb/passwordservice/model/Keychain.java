package dev.twiceb.passwordservice.model;

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
    private List<PasswordChangeLog> passwordChangeLogs = new ArrayList<>();

    @OneToOne(mappedBy = "keychain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private PasswordComplexityMetric passwordComplexityMetric;

    @OneToOne(mappedBy = "keychain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserPasswordExpirySetting userPasswordExpirySetting;

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
    private DomainStatus status;

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
