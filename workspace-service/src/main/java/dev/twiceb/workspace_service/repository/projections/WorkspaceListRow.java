package dev.twiceb.workspace_service.repository.projections;

import java.util.UUID;

public interface WorkspaceListRow {
    UUID getId();

    String getName();

    String getSlug();

    UUID getOwnerId();

    Integer getRole();

    Integer getTotalMembers();
}
