package dev.twiceb.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "password_reset_codes")
public class PasswordResetCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hashed_code")
    private String hashedCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "expiration_time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "reset_count")
    private int resetCount = 0;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modified_date", columnDefinition = "TIMESTAMP")
    private LocalDateTime modifiedDate;
}
