package dev.twiceb.workspace_service.model;

import java.time.ZoneId;
import java.util.UUID;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import dev.twiceb.workspace_service.model.support.SlugNotRestricted;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "workspaces")
@SQLDelete(sql = "UPDATE workspaces SET deleted_at = now() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
public class Workspace extends AuditableEntity {

    @Column(name = "instance_id", nullable = false)
    private UUID instanceId;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "name", nullable = false, length = 80)
    private String name;

    @SlugNotRestricted
    @Column(name = "slug", nullable = false, unique = true, length = 46)
    private String slug;

    @Column(name = "organization_size", length = 20)
    private String organizationSize;

    @Column(name = "timezone", nullable = false, length = 255)
    private String timezone = "UTC";

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    public void validateTimezone() {
        try {
            ZoneId.of(timezone);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid timezone: " + timezone);
        }
    }

    @PrePersist
    @PreUpdate
    public void beforeSave() {
        validateTimezone();
    }

    @Override
    public String toString() {
        return name;
    }
}
