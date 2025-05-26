package dev.twiceb.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "password_reset_otp")
public class PasswordResetOtp {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "hashed_otp")
    private String hashedOtp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiration_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modified_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime modifiedDate = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.modifiedDate = LocalDateTime.now();
    }
}
