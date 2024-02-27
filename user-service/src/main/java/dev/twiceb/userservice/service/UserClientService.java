package dev.twiceb.userservice.service;

import dev.twiceb.common.dto.response.UserDeviceResponse;

public interface UserClientService {

    String getUserEmail(Long userId);
    void increaseNotificationCount(Long userId);
    void decreaseNotificationCount(Long userId);
    void resetNotificationCount(Long userId);
    UserDeviceResponse getUserDevice(Long userId);
}
