package dev.twiceb.instanceservice.domain.projection;

import java.util.UUID;

public interface InstanceConfigVersionProjection {
    UUID getId();

    Long getConfigVersion();
}
