package dev.twiceb.passwordservice.model;

import java.time.LocalDate;
import java.sql.Date;

import org.hibernate.annotations.ColumnTransformer;

import dev.twiceb.passwordservice.enums.DomainStatus;
import dev.twiceb.passwordservice.service.util.DomainStatusConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encryption_key_id")
    private EncryptionKey encryptionKey;

    @Column(nullable = false)
    private String domain;

    @Column(name = "encrypted_password", columnDefinition = "bytea")
    private byte[] password;

    @Transient
    private String fakePassword;

    @Column(name = "status")
    @Convert(converter = DomainStatusConverter.class)
    private DomainStatus status;

    @Column(name = "update_date", nullable = false)
    private Date date;

    public Keychain() {
    }

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

        System.out.println("Before setting status: " + this.status); // Print before setting the status
        this.date = Date.valueOf(date90DaysFromNow);
        System.out.println("DomainStatus.ACTIVE: " + DomainStatus.ACTIVE); // Print DomainStatus.ACTIVE to compare
        this.status = DomainStatus.ACTIVE;
        System.out.println("After setting status: " + this.status);
    }

    public String getFakePassword() {
        return "**********";
    }

}
