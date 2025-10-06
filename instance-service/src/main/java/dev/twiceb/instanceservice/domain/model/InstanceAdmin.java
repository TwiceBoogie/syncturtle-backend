package dev.twiceb.instanceservice.domain.model;

import java.time.Instant;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "instance_admins")
public class InstanceAdmin extends AuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", nullable = false)
    private Instance instance;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "role", nullable = false)
    private int role;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Column(name = "deleted_at", nullable = true)
    private Instant deletedAt;

    protected InstanceAdmin() {} // jpa-friendly

    public static InstanceAdmin create(Instance instance, UUID userId) {
        InstanceAdmin iAdmin = new InstanceAdmin();
        iAdmin.instance = instance;
        iAdmin.userId = userId;
        iAdmin.role = 20;
        iAdmin.verified = false;

        return iAdmin;
    }
}
