package dev.twiceb.userservice.model;

import dev.twiceb.common.model.AuditableEntity;
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

    @Column(name = "hashed_code", nullable = false)
    private String hashedCode;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public ActivationCode() {
    }

    public ActivationCode(String hashedCode, LocalDateTime expirationTime, User user) {
        this.hashedCode = hashedCode;
        this.expirationTime = expirationTime;
        this.user = user;
    }
}
