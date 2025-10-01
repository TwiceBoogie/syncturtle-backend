package dev.twiceb.instanceservice.domain.model;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UuidGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "tenant_members",
        indexes = {@Index(name = "idx_tm_tenant", columnList = "tenant_id"),
                @Index(name = "idx_tm_member", columnList = "member_id"),
                @Index(name = "idx_tm_user_active", columnList = "member_id, is_active")})
@SQLDelete(sql = "UPDATE tenant_members SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class TenantMember {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // TenantMember -> Tenant (tenant can have many members)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    // logical fk to user-service.users.id
    @Column(name = "member_id", nullable = false, columnDefinition = "uuid")
    private UUID memberId;

    @Column(name = "role", nullable = false)
    private Integer role;

    @Column(name = "company_role")
    private String companyRole;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "deleted_at")
    private Instant deletedAt;
}
