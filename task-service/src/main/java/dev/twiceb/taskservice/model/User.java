package dev.twiceb.taskservice.model;

import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

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
    private Set<Task> tasks = new HashSet<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public User() {}

    public User(UUID userId, UserStatus userStatus, UserRole role) {
        this.id = userId;
        this.userStatus = userStatus;
        this.role = role;
    }
}
