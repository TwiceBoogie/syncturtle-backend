package dev.twiceb.userservice.repository.projection;

public interface UserDeviceProjection {
    Long getUserId();

    Long getUserDeviceId();

    String getDeviceKey();

    String getIpAddress();
}
