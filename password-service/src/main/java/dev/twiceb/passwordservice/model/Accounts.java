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
    private Long user_id;

}
