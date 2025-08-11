package dev.twiceb.userservice.model;

import dev.twiceb.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.annotations.ColumnTransformer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"}),
        @UniqueConstraint(columnNames = {"username"})})
public class User extends AuditableEntity {

    @Id
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "username", length = 128, nullable = false, unique = true)
    private String username = UUID.randomUUID().toString();

    @Column(name = "email", length = 255, nullable = false, unique = true)
    private String email;

    @Column(name = "first_name", length = 255, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 255, nullable = false)
    private String lastName;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "mobile_phone", length = 255)
    private String mobilePhone;

    @Column(name = "display_name", nullable = false)
    private String displayName = "";

    private String token;
    private Instant tokenUpdatedAt;

    @Column(name = "about")
    private String about;

    @Column(name = "birthday")
    private String birthday;

    @Column(name = "gender")
    private String gender;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(read = "user_status::text", write = "?::user_status")
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Column(name = "notification_count")
    private int notificationCount = 0;

    @Column(name = "notify_password_change")
    private boolean notifyPasswordChange = true;

    private Instant dateJoined;
    private String createdLocation;
    private String lastLocation;

    // Flags
    private boolean isSuperUser = false;
    private boolean isManaged = false;
    private boolean isPasswordExpired = false;
    private boolean isActive = true;
    private boolean isStaff = false;
    private boolean isEmailVerified = false;
    private boolean isPasswordAutoSet = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<PasswordResetOtp> passwordResetOtps = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "login_attempt_policy")
    private LoginAttemptPolicy loginAttemptPolicy;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER,
            orphanRemoval = true)
    private List<UserDevice> userDevices = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<LockedUser> lockoutHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            orphanRemoval = true)
    private List<UserProfile> userProfiles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            orphanRemoval = true)
    private UserProfileLimt userProfileLimt;

    public User() {}

    @PrePersist
    @PreUpdate
    private void handleUser() {
        // normalize email
        if (email != null) {
            email = email.trim().toLowerCase();
        }

        if (tokenUpdatedAt != null) {
            token = UUID.randomUUID().toString().replace("-", "")
                    + UUID.randomUUID().toString().replace("-", "");
            tokenUpdatedAt = Instant.now();
        }

        // auto generate display name
        if (displayName == null || displayName.isBlank()) {
            if (email != null && email.contains("@")) {
                displayName = email.split("@")[0];
            } else {
                displayName = RandomStringUtils.randomAlphanumeric(6);
            }
        }

        if (isSuperUser) {
            isStaff = true;
        }
    }

}
