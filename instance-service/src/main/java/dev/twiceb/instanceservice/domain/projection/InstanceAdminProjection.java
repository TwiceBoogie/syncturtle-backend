package dev.twiceb.instanceservice.domain.projection;

import java.time.Instant;
import java.util.UUID;

public interface InstanceAdminProjection {
    UUID getId();

    UUID getInstance();

    UserLiteViewProjection getUser();

    int getRole();

    Instant getUpdatedAt();

    Instant getCreatedAt();
}
