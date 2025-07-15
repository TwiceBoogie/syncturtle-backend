package dev.twiceb.userservice.model;

import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "username"})})
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "about")
    private String about;

    @Column(name = "first_name")
    private String firstName = "";

    @Column(name = "last_name")
    private String lastName = "";

    @Column(name = "username")
    private String username = "";

    @Column(name = "password")
    private String password;

    @Column(name = "is_password_autoset")
    private boolean isPasswordAutoSet = false;

    @Column(name = "birthday")
    private String birthday;

    @Column(name = "gender")
    private String gender;

    @Column(name = "country")
    private String country;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "phone")
    private Long phone;

    @Column(name = "verified", columnDefinition = "boolean default false")
    private boolean verified = false;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(read = "role::text", write = "?::user_role")
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(read = "user_status::text", write = "?::user_status")
    private UserStatus userStatus = UserStatus.ACTIVE;

    @Column(name = "notification_count")
    private int notificationCount = 0;

    @Column(name = "notify_password_change")
    private boolean notifyPasswordChange = true;

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

}
