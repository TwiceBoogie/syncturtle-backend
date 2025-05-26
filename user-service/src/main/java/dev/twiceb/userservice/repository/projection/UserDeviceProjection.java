package dev.twiceb.userservice.repository.projection;

import java.util.UUID;

public interface UserDeviceProjection {
    UUID getUserId();

    UUID getUserDeviceId();

    String getDeviceKey();

    String getIpAddress();
}
