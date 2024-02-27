package dev.twiceb.userservice.model;

import dev.twiceb.common.model.AuditableEntity;
import dev.twiceb.userservice.enums.ActivationCodeType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "activation_codes")
public class ActivationCode extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "code_type", nullable = false)
    private ActivationCodeType codeType;

    @Column(name = "hashed_code", nullable = false)
    private String hashedCode;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public ActivationCode() {
    }

    public ActivationCode(String hashedCode, ActivationCodeType codeType, User user) {
        this.hashedCode = hashedCode;
        this.codeType = codeType;
        this.user = user;
    }

    @PrePersist
    public void prePersist() {
        if (this.codeType.equals(ActivationCodeType.ACTIVATION)) {
            this.expirationTime = LocalDateTime.now().plusHours(24);
        } else if (this.codeType.equals(ActivationCodeType.DEVICE_VERIFICATION)) {
            this.expirationTime = LocalDateTime.now().plusMinutes(5);
        } else if (this.codeType.equals(ActivationCodeType.PASSWORD_RESET)) {
            this.expirationTime = LocalDateTime.now().plusMinutes(10);
        }
    }
}
