package dev.twiceb.passwordservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Setter
@Getter
@Table(name = "password_complexity_metrics")
public class PasswordComplexityMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "keychain_id")
    private Keychain keychain;

    @Column(name = "password_length", nullable = false)
    private int passwordLength;

    @Column(name = "character_types_used", nullable = false)
    private int characterTypesUsed;

    @Column(name = "entropy", nullable = false)
    private double entropy;

    @Column(name = "check_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp checkDate;

    public PasswordComplexityMetric() {}

    public PasswordComplexityMetric(
            int passwordLength,
            int characterTypesUsed,
            double entropy
    ) {
        this.passwordLength = passwordLength;
        this.characterTypesUsed = characterTypesUsed;
        this.entropy = entropy;
    }
}
