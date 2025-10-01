package dev.twiceb.userservice.domain.model.readmodel;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SQLRestriction;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tenant_memberships_view")
@Immutable
@SQLRestriction("is_active <> TRUE")
public class TenantMembershipView {

    @EmbeddedId
    private TenantMembershipViewId id;

    @Column(name = "role", nullable = false)
    private int role;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "version", nullable = false)
    private long version;

    public UUID getTenantId() {
        return id.getTenantId();
    }

    public UUID getUserId() {
        return id.getUserId();
    }
}
