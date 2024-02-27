package dev.twiceb.passwordservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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

    @Column(name = "dictionary_word_count", nullable = false)
    private int dictionaryWordCount;

    @Column(name = "numeric_characters_count", nullable = false)
    private int numericCharactersCount;

    @Column(name = "special_characters_count", nullable = false)
    private int specialCharactersCount;

    @Column(name = "uppercase_letters_count", nullable = false)
    private int uppercaseLettersCount;

    @Column(name = "lowercase_letters_count", nullable = false)
    private int lowercaseLettersCount;

    @Column(name = "sequential_characters_count", nullable = false)
    private int sequentialCharactersCount;

    @Column(name = "repeating_characters_count", nullable = false)
    private int repeatingCharactersCount;

    @Column(name = "password_complexity_score")
    private Double passwordComplexityScore;

    @Column(name = "check_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime checkDate = LocalDateTime.now();
}
