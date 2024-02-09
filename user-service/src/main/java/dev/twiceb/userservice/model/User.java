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
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "password")
    private String password;

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

    @OneToOne
    @JoinColumn(name = "login_attempt_policy")
    private LoginAttemptPolicy loginAttemptPolicy;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UserDevice> userDevices = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<LockedUser> lockedUser = new ArrayList<>();

    public User() {
    }

    public User(String email, String firstName, String lastName, String password, LoginAttemptPolicy policy) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.loginAttemptPolicy = policy;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", password='" + password + '\'' +
                ", active=" + verified +
                ", role=" + role +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return verified == user.verified &&
                Objects.equals(id, user.id) &&
                Objects.equals(email, user.email) &&
                Objects.equals(firstName, user.firstName) &&
                Objects.equals(lastName, user.lastName) &&
                Objects.equals(password, user.password) &&
                Objects.equals(userStatus, user.userStatus) &&
                role == user.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, firstName, lastName, password, verified, userStatus, role);
    }
}

