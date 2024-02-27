package dev.twiceb.userservice.service;

import dev.twiceb.common.event.PasswordChangeEvent;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.projection.UserDeviceProjection;

import java.time.LocalDateTime;
import java.util.Map;

public interface UserService {

    User getUserById(Long userId);
    UserDeviceProjection getUserDeviceDetails(Long userId);
    Map<String, String> updateUserProfile(User userInfo);
}
