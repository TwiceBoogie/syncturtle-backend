package dev.twiceb.userservice.service;

import dev.twiceb.common.dto.response.UserDeviceResponse;
import dev.twiceb.userservice.domain.projection.UserPrincipalProjection;
import java.util.UUID;

public interface UserClientService {

    String getUserEmail(UUID userId);

    void increaseNotificationCount(UUID userId);

    void decreaseNotificationCount(UUID userId);

    void resetNotificationCount(UUID userId);

    UserDeviceResponse getUserDevice(UUID userId);

    UserPrincipalProjection getUserPrincipal(UUID userId);

    UUID findUserIdByEmail(String email);
}
