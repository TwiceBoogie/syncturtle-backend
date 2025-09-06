package dev.twiceb.userservice.service;

import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.projection.UserDeviceProjection;
import java.util.Map;
import java.util.UUID;

public interface UserService {

    User getUserById(UUID userId);

    UserDeviceProjection getUserDeviceDetails(UUID userId);

    Map<String, String> updateUserProfile(User userInfo);
}
