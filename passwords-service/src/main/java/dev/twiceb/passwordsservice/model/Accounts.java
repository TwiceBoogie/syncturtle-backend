package dev.twiceb.passwordsservice.model;

import java.time.LocalDate;
import java.sql.Date;

import org.hibernate.annotations.ColumnTransformer;

import dev.twiceb.passwordsservice.enums.DomainStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Accounts {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String domain;

    @Column(nullable = false)
    private String email;

    @Transient
    private String fakePassword;

    @Column(name = "status")
    @ColumnTransformer(read = "status::text", write = "?::domain_Status")
    private DomainStatus status;

    @Column(name = "update_date", nullable = false)
    private Date date;

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
