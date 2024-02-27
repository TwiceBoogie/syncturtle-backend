package dev.twiceb.passwordservice.model;

import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(read = "user_status::text", write = "?::user_status")
    private UserStatus userStatus;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "role::text",
            write = "?::user_role"
    )
    private UserRole role = UserRole.USER;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<EncryptionKey> encryptionKeys = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<PasswordReuseStatistic> passwordReuseStatistics = new ArrayList<>();

    public User() {}

    public User(Long id, String fullName, String username, UserStatus userStatus, UserRole role) {
        this.id = id;
        this.fullName = fullName;
        this.username = username;
        this.userStatus = userStatus;
        this.role = role;
    }
}
