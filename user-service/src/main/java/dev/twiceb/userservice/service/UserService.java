package dev.twiceb.userservice.service;

import dev.twiceb.common.event.PasswordChangeEvent;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.projection.UserDeviceProjection;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public interface UserService {

    User getUserById(UUID userId);
    UserDeviceProjection getUserDeviceDetails(UUID userId);
    Map<String, String> updateUserProfile(User userInfo);
}
