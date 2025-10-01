package dev.twiceb.instanceservice.domain.projection;

import java.time.Instant;

public interface InstanceStatusProjection {
    boolean isSetupDone();

    String getEdition();

    Instant getUpdatedAt();
}
