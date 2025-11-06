package dev.twiceb.workspace_service.repository.projections;

import java.time.Instant;
import java.util.UUID;

public interface UserProjection {
    UUID getId();

    String getEmail();

    String getFirstName();

    String getLastName();

    String getDisplayName();

    Instant getDateJoined();
}
