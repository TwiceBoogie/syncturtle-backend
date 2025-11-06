package dev.twiceb.workspace_service.model;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users_lite")
public class User {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "date_joined", nullable = false)
    private Instant dateJoined;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
