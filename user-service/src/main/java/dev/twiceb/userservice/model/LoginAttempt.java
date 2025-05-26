package dev.twiceb.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "login_attempts")
public class LoginAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "attempt_timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime attemptTimestamp = LocalDateTime.now();

    @Column(name = "success")
    private boolean success = false;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "is_new_device")
    private boolean isNewDevice = false;
}
