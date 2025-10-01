package dev.twiceb.workspace_service.model;

import java.time.Instant;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import dev.twiceb.workspace_service.enums.WorkspaceRole;
import dev.twiceb.workspace_service.model.support.WorkspaceRoleConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "workspace_member_invites")
@SQLDelete(sql = "UPDATE workspace_member_invites SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WorkspaceMemberInvite extends AuditableEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Column(name = "email", nullable = false, length = 255)
    private String email;

    @Column(name = "accepted", nullable = false)
    private boolean accepted = false;

    @Column(name = "token", nullable = false, length = 255)
    private String token;

    @Column(name = "message")
    private String message;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @Convert(converter = WorkspaceRoleConverter.class)
    @Column(name = "role", nullable = false)
    private WorkspaceRole role = WorkspaceRole.GUEST;

    @Override
    public String toString() {
        return workspace.getName() + " " + email + " " + accepted;
    }
}
