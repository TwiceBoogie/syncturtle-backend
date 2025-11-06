package dev.twiceb.workspace_service.repository.projections;

import java.time.Instant;
import java.util.UUID;

public interface WorkspaceProjection {
    UUID getId();

    UserProjection getUser();

    String getName();

    String getSlug();

    Integer getOrganizationSize();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    UUID getCreatedBy();

    UUID getUpdatedBy();
}
