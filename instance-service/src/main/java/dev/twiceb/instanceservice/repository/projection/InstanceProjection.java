package dev.twiceb.instanceservice.repository.projection;

import java.util.UUID;
import dev.twiceb.instanceservice.enums.InstanceEdition;

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
