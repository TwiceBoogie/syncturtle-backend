package dev.twiceb.userservice.domain.projection;

import java.util.UUID;

public interface DevicePrincipleProjection {
    UUID getId();

    String getDeviceKey();
}
