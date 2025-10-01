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
@Table(name = "tenant_member_invites",
        indexes = {@Index(name = "idx_ti_tenant", columnList = "tenant_id"),
                @Index(name = "idx_ti_email_pending", columnList = "email")})
@SQLDelete(sql = "UPDATE tenant_member_invites SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class TenantMemberInvite extends AuditableEntity {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "email", nullable = false, columnDefinition = "citext")
    private String email;

    @Column(name = "accepted", nullable = false)
    private boolean accepted;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @Column(name = "message")
    private String message;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @Column(name = "role", nullable = false)
    private Integer role;
}
