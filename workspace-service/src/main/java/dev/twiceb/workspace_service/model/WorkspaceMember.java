package dev.twiceb.workspace_service.model;

import java.util.UUID;
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
@Table(name = "workspace_members")
@SQLDelete(sql = "UPDATE workspace_members SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class WorkspaceMember extends AuditableEntity {

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    // logical FK to user-service.users.id
    @Column(name = "member_id", nullable = false)
    private UUID memberId;

    @Convert(converter = WorkspaceRoleConverter.class)
    @Column(name = "role", nullable = false)
    private WorkspaceRole role = WorkspaceRole.GUEST;

    @Column(name = "company_role")
    private String companyRole;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Override
    public String toString() {
        return memberId + " <" + workspace.getName() + ">";
    }
}
