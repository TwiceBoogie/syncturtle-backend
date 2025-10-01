package dev.twiceb.userservice.domain.model.readmodel;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.Immutable;
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
@Table(name = "tenant_invites_view")
@Immutable
public class TenantInviteView {

    @EmbeddedId
    private TenantInviteViewId id;

    @Column(name = "role", nullable = false)
    private int role;

    @Column(name = "accepted", nullable = false)
    private boolean accepted;

    @Column(name = "responded_at")
    private Instant respondedAt;

    public UUID getTenantId() {
        return id.getTenantId();
    }

    public String getEmail() {
        return id.getEmail();
    }
}
