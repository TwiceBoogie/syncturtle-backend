package dev.twiceb.userservice.domain.model;

import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;
import org.hibernate.annotations.UuidGenerator;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends AuditableEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "username", length = 128, nullable = false)
    private String username;

    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Column(name = "first_name", length = 36, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 36, nullable = false)
    private String lastName;

    @Column(name = "password", length = 255)
    private String password;

    @Column(name = "mobile_phone", length = 32)
    private String mobilePhone;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", length = 32, nullable = false)
    private UserStatus userStatus;

    @Column(name = "notification_count", nullable = false)
    private long notificationCount;

    @Column(name = "notify_password_change", nullable = false)
    private boolean notifyPasswordChange;

    // ====== DENORMALIZED ======
    // denormalized last-known snapshotss
    // so we don't call loginAttemptRepo
    @Column(name = "last_login_time")
    private Instant lastLoginTime;

    @Column(name = "last_login_medium", length = 32)
    private String lastLoginMedium;

    @Column(name = "last_active")
    private Instant lastActive;
    // ====== DENORMALIZED ======

    // Flags
    @Column(name = "is_password_autoset", nullable = false)
    private boolean isPasswordAutoSet;

    @Column(name = "is_password_expired", nullable = false)
    private boolean isPasswordExpired;

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    // User -> LoginAttemptPolicy (child = User owns FK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // multi users can share a policy
    @JoinColumn(name = "login_policy_id", nullable = false)
    private LoginPolicy loginPolicy;

    protected User() {} // jpa friendly

    public User(String email, String username) {
        this.email = normalize(email);
        this.username = (username == null || username.isBlank())
                ? UUID.randomUUID().toString().replace("-", "")
                : username;
        this.displayName = deriveDisplayName(this.email);
        this.userStatus = UserStatus.ACTIVE;
        this.notificationCount = 0L;
        this.notifyPasswordChange = true;
        this.isPasswordExpired = false;
        this.isActive = true;
    }

    private User(String email, String passwordHash, AuthMedium method, LoginPolicy policyRef,
            String firstName, String lastName) {
        if (policyRef == null) {
            throw new IllegalArgumentException("Policy reference is required");
        }

        String normalizedEmail = normalize(email);
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must be valid");
        }

        if (method == AuthMedium.PASSWORD) {
            if (passwordHash == null || passwordHash.isBlank()) {
                throw new IllegalArgumentException(
                        "Password hash required for PASSWORD signup/signin");
            }
            this.isPasswordAutoSet = false;
            this.password = passwordHash;
            this.isEmailVerified = false;
        } else {
            // must be passwordless
            if (passwordHash != null && !passwordHash.isBlank()) {
                throw new IllegalArgumentException(
                        "Password must be empty for passwordless signup/signin");
            }
            this.isPasswordAutoSet = true;
            this.password = UUID.randomUUID().toString();
            // the magic-link was sent through email so by default its verified
            this.isEmailVerified = true;
        }

        this.email = normalizedEmail;
        this.displayName = deriveDisplayName(normalizedEmail);
        this.username = UUID.randomUUID().toString().replace("-", "");
        this.userStatus = UserStatus.ACTIVE;
        this.notificationCount = 0;
        this.notifyPasswordChange = true;
        this.isPasswordExpired = false;
        this.isActive = true;
        this.loginPolicy = policyRef;
        this.firstName = nvl(firstName, "");
        this.lastName = nvl(lastName, "");
    }

    public static User createWithPasswordAdmin(String email, String passwordHash,
            LoginPolicy policyRef, String firstName, String lastName) {
        return new User(email, passwordHash, AuthMedium.PASSWORD, policyRef, firstName, lastName);
    }

    // factory for email/password users
    public static User createWithPassword(String email, String passwordHash,
            LoginPolicy policyRef) {
        return new User(email, passwordHash, AuthMedium.PASSWORD, policyRef, null, null);
    }

    // factory for magic/passwordless users
    public static User createPasswordless(String email, LoginPolicy policyRef) {
        return new User(email, null, AuthMedium.MAGIC_LINK, policyRef, null, null);
    }

    public void handleUserStatus(UserStatus status) {
        this.userStatus = status;
    }

    public void resetValidPassword(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash required for PASSWORD signup/signin");
        }
        // if user is passwordless, still reset/rotate password
        this.password = passwordHash;
    }

    public void verifyEmail(String email) {
        String normalizedEmail = normalize(email);
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must be valid");
        }
        this.email = normalizedEmail;
        this.isEmailVerified = true;
    }

    public void completeOnboarding(String firstName, String lastName, String about, String gender) {
        if (firstName == null || firstName.isBlank() || firstName.length() > 36) {
            throw new IllegalArgumentException("Invalid first name");
        }
        if (lastName == null || lastName.isBlank() || lastName.length() > 36) {
            throw new IllegalArgumentException("Invalid first name");
        }

        this.firstName = firstName;
        this.lastName = lastName;
    }

    private static String deriveDisplayName(String email) {
        if (email == null) {
            return RandomStringUtils.randomAlphabetic(6);
        }
        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : RandomStringUtils.randomAlphabetic(6);
    }

    private static String normalize(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private static String nvl(String val, String def) {
        if (val == null || val.isBlank()) {
            return def;
        }
        return val;
    }

}
