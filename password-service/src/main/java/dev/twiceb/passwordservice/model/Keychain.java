package dev.twiceb.passwordservice.model;

import java.time.LocalDate;
import java.sql.Date;

import org.hibernate.annotations.ColumnTransformer;

import dev.twiceb.passwordservice.enums.DomainStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "unique_account_domain", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "account_id", "domain" })
})
public class Keychain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Accounts account;

    @ManyToOne
    @JoinColumn(name = "encryption_key_id")
    private EncryptionKey encryptionKey;

    @Column(nullable = false)
    private String domain;

    @Column(name = "encrypted_password", columnDefinition = "bytea")
    private byte[] password;

    @Transient
    private String fakePassword;

    @Column(name = "status")
    @ColumnTransformer(read = "status::text", write = "?::domain_Status")
    private DomainStatus status;

    @Column(name = "update_date", nullable = false)
    private Date date;

    public Keychain() {}

    public Keychain(Accounts account, EncryptionKey encryptionKey, String domain, byte[] password) {
        this.account = account;
        this.encryptionKey = encryptionKey;
        this.domain = domain;
        this.password = password;
    }

    @PrePersist
    private void prePersist() {
        LocalDate currentTime = LocalDate.now();
        LocalDate date90DaysFromNow = currentTime.plusDays(90);

        this.date = Date.valueOf(date90DaysFromNow);
        this.status = DomainStatus.ACTIVE;
    }

    public String getFakePassword() {
        return "**********";
    }
}
