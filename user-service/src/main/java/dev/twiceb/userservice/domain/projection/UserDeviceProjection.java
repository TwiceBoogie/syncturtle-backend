package dev.twiceb.userservice.domain.projection;

import java.util.UUID;

public interface UserDeviceProjection {
    UUID getUserId();

    UUID getUserDeviceId();

    String getDeviceKey();

    String getIpAddress();
}
