package dev.twiceb.instanceservice.domain.projection;

import java.util.UUID;
import dev.twiceb.instanceservice.domain.enums.InstanceEdition;

public interface InstanceProjection {
    UUID getId();

    String getSlug();

    String getName();

    InstanceEdition getEdition();

    String getCurrentVersion();

    String getDomain();

    String getNamespace();

    boolean isSetupDone();

    boolean isVerified();

    boolean isTest();
}
