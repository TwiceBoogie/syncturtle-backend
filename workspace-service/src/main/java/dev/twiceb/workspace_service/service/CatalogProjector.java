package dev.twiceb.workspace_service.service;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.enums.InstanceEdition;

public interface CatalogProjector {
    void applyInstanceUpsert(UUID instanceId, String slug, InstanceEdition edition, long version,
            Instant updatedAt);

    void applyPlanUpsert(UUID planId, long version, Instant updatedAt);
}
