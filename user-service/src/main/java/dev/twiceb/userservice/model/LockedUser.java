package dev.twiceb.userservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "locked_users")
public class LockedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lockout_start", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime lockoutStart = LocalDateTime.now();

    @Column(name = "lockout_end", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime lockoutEnd;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "lockout_reason", nullable = false)
    private String lockoutReason;

    @Column(name = "is_requested_by_user")
    private boolean isRequestedByUser = false;

    @PrePersist
    public void prePersist() {
        LocalDateTime currentTime = LocalDateTime.now();
        this.lockoutEnd = currentTime.plusMinutes(
                this.user.getLoginAttemptPolicy().getLockoutDuration().toMinutes()
        );
    }
}
