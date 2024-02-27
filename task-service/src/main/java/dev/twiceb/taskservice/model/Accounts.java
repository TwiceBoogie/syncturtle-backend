package dev.twiceb.taskservice.model;

import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Entity
@Getter
@Setter
public class Accounts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

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

    public Accounts() {}

    public Accounts(Long userId, UserStatus userStatus, UserRole role) {
        this.userId = userId;
        this.userStatus = userStatus;
        this.role = role;
    }
}
