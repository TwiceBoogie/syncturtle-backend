package dev.twiceb.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "token")
    private String token;

    @Column(name = "expirationTime", nullable = false)
    LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate = LocalDateTime.now();

    public PasswordResetToken() {}

    public PasswordResetToken(String token, User user) {
        this.token = token;
        this.user = user;
    }

    @PreUpdate
    public void preUpdate() {this.modifiedDate = LocalDateTime.now();}
}
