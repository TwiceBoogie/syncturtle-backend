package dev.twiceb.userservice.domain.model;

import static dev.twiceb.common.util.StringHelper.*;

import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.userservice.domain.model.support.UserRoleConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // jpa friendly
public class User extends AuditableEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Size(max = 128)
    @Column(name = "username", length = 128, nullable = false)
    private String username;

    @Email
    @Size(max = 255)
    @Column(name = "email", length = 255, nullable = false)
    private String email;

    @Size(max = 36)
    @Column(name = "first_name", length = 36, nullable = false)
    private String firstName;

    @Size(max = 36)
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

    @Convert(converter = UserRoleConverter.class)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "notification_count", nullable = false)
    private long notificationCount;

    @Column(name = "notify_password_change", nullable = false)
    private boolean notifyPasswordChange;

    @CreatedDate
    @Column(name = "date_joined", nullable = false)
    private Instant dateJoined;

    // ====== DENORMALIZED ======
    // denormalized last-known snapshotss
    // so we don't call loginAttemptRepo
    @Column(name = "last_login_time")
    private Instant lastLoginTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "last_login_medium", length = 32)
    private AuthMedium lastLoginMedium;

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
    private boolean active;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    // User -> LoginAttemptPolicy (child = User owns FK)
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // multi users can share a policy
    @JoinColumn(name = "login_policy_id", nullable = false)
    private LoginPolicy loginPolicy;

    public static User createWithPasswordAdmin(String email, String passwordHash,
            LoginPolicy policyRef, String firstName, String lastName) {
        requirePolicy(policyRef);
        requirePassword(passwordHash);
        return baseCreate(email, passwordHash, AuthMedium.PASSWORD, policyRef, firstName, lastName,
                false, UserRole.SUPER_USER);
    }

    // factory for email/password users
    public static User createWithPassword(String email, String passwordHash,
            LoginPolicy policyRef) {
        requirePolicy(policyRef);
        requirePassword(passwordHash);
        return baseCreate(email, passwordHash, AuthMedium.PASSWORD, policyRef, "", "", false,
                UserRole.USER);
    }

    // factory for magic/passwordless users
    public static User createPasswordless(String email, LoginPolicy policyRef) {
        requirePolicy(policyRef);
        return baseCreate(email, null, AuthMedium.MAGIC_LINK, policyRef, "", "", true,
                UserRole.USER);
    }

    private static User baseCreate(String email, String passwordHash, AuthMedium method,
            LoginPolicy policyRef, String firstName, String lastName, boolean autoSetPassword,
            UserRole userRole) {
        String normEmail = normalizeEmail(email);
        if (isBlank(normEmail) || !isEmailish(normEmail)) {
            throw new IllegalArgumentException("Email must be valid");
        }

        User user = new User();
        user.email = normEmail;
        user.displayName = deriveDisplayNameFromEmail(normEmail);
        user.username = generateRandomUsername();
        user.userStatus = UserStatus.ACTIVE;
        user.role = userRole;
        user.notificationCount = 0L;
        user.notifyPasswordChange = true;
        user.isPasswordExpired = false;
        user.active = true;
        user.loginPolicy = policyRef;
        user.firstName = hasLengthAtMost(firstName, 36) ? firstName : truncate(firstName, 36);
        user.lastName = hasLengthAtMost(lastName, 36) ? lastName : truncate(lastName, 36);

        if (method == AuthMedium.PASSWORD) {
            user.isPasswordAutoSet = false;
            user.password = passwordHash;
            user.isEmailVerified = false;
        } else {
            user.isPasswordAutoSet = true;
            user.password = randomHex(32);
            user.isEmailVerified = true;
        }

        return user;
    }

    public void handleUserStatus(UserStatus status) {
        this.userStatus = status;
    }

    public void resetValidPassword(String passwordHash) {
        requirePassword(passwordHash);
        // if user is passwordless, still reset/rotate password
        this.password = passwordHash;
        this.isPasswordAutoSet = false;
        this.isPasswordExpired = false;
    }

    public void verifyEmail(String email) {
        String norm = normalizeEmail(email);
        if (isBlank(norm) || !isEmailish(norm)) {
            throw new IllegalArgumentException("Email must be valid");
        }
        this.email = norm;
        this.isEmailVerified = true;
    }

    public void completeOnboarding(String firstName, String lastName) {
        if (isBlank(firstName) || !hasLengthAtMost(firstName, 36)) {
            throw new IllegalArgumentException("Invalid first name");
        }
        if (isBlank(lastName) || !hasLengthAtMost(lastName, 36)) {
            throw new IllegalArgumentException("Invalid first name");
        }

        this.firstName = collapseWhitespace(firstName);
        this.lastName = collapseWhitespace(lastName);
    }

    public void markLastLogin(AuthMedium medium, Instant when) {
        this.lastLoginMedium = medium;
        this.lastLoginTime = when;
        this.lastActive = when;
    }

    public void setActive(boolean val) {
        this.active = val;
    }

    private static void requirePolicy(LoginPolicy policyRef) {
        if (policyRef == null) {
            throw new IllegalArgumentException("Policy reference is required");
        }
    }

    private static void requirePassword(String passwordHash) {
        if (isBlank(passwordHash)) {
            throw new IllegalArgumentException("Password hash required for PASSWORD signup/signin");
        }
    }
}
