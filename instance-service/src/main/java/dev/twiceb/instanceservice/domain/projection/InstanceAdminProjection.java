package dev.twiceb.instanceservice.domain.projection;

import java.util.UUID;

public interface InstanceAdminProjection {
    UUID getId();

    OnlyId getInstance();

    UUID getUserId();

    int getRole();

    default UUID getInstanceId() {
        return getInstance().getId();
    }
}
