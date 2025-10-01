package dev.twiceb.userservice.domain.projection;

import java.util.UUID;

public interface DevicePrincipalProjection {
    UUID getId();

    String getDeviceKey();
}
