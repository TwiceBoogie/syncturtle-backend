package dev.twiceb.passwordservice.model;

import dev.twiceb.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.sql.Timestamp;

@Entity
@Setter
@Getter
@Table(name = "password_change_logs")
public class PasswordChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "keychain_id")
    private Keychain keychain;

    @Enumerated(EnumType.STRING)
    @ColumnTransformer(
            read = "changedByUser::text",
            write = "?::changed_by_user"
    )
    private UserRole changedByUser = UserRole.USER;

    @Column(name = "change_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp changeDate;
}
