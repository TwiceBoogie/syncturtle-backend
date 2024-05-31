package dev.twiceb.passwordservice.model;

import dev.twiceb.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;

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
    @ColumnTransformer(read = "changed_by_user::text", write = "?::user_role")
    private UserRole changedByUser = UserRole.USER;

    @Column(name = "change_date", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime changeDate = LocalDateTime.now();

    @Column(name = "change_reason")
    private String changeReason;

    @Column(name = "change_type")
    private String changeType;

    @Column(name = "change_success")
    private boolean changeSuccess = true;

    @Column(name = "change_result")
    private String changeResult = "pending";

    @Column(name = "user_device_id", nullable = false)
    private Long userDeviceId;
}
