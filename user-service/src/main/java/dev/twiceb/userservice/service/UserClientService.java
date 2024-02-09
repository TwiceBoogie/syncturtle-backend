package dev.twiceb.userservice.service;

public interface UserClientService {

    String getUserEmail(Long userId);
    void increaseNotificationCount(Long userId);
    void decreaseNotificationCount(Long userId);
    void resetNotificationCount(Long userId);
}
