package dev.twiceb.instanceservice.domain.projection;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.enums.InstanceEdition;

public interface InstanceProjection {
    UUID getId();

    String getSlug();

    String getInstanceName();

    String getCurrentVersion();

    String getLatestVersion();

    InstanceEdition getEdition();

    String getDomain();

    Instant getLastCheckedAt();

    String getNamespace();

    boolean isSetupDone();

    boolean isVerified();

    boolean isTest();

    Instant getCreatedAt();

    Instant getUpdatedAt();
}
