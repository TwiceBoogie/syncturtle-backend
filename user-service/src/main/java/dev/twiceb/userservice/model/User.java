package dev.twiceb.userservice.model;

import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "username"})
})
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "about")
    private String about;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PasswordResetOtp> passwordResetOtps = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "login_attempt_policy")
    private LoginAttemptPolicy loginAttemptPolicy;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserDevice> userDevices = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LockedUser> lockoutHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserProfile> userProfiles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserProfileLimt userProfileLimt;

    public User() {
    }

    public User(
            String email, String firstName, String lastName, String username, String password, LoginAttemptPolicy policy
    ) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.loginAttemptPolicy = policy;
    }

}

