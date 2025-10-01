package dev.twiceb.userservice.domain.model.readmodel;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.SQLRestriction;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tenants_view")
@Immutable
@SQLRestriction("deleted_at IS NULL")
public class TenantView {

    @Id
    @Column(name = "tenant_id", nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "slug", nullable = false, length = 100)
    private String slug;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "instance_id", nullable = false, columnDefinition = "uuid")
    private UUID instanceId;

    @Column(name = "plan_key", length = 50)
    private String planKey;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    @Column(name = "version", nullable = false)
    private long version;
}
