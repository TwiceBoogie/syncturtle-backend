package dev.twiceb.userservice.service;

import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.userservice.domain.enums.LoginContext;
import dev.twiceb.userservice.domain.model.User;

/**
 * Service interface for handling login attempts and related user actions.
 */
public interface LoginService {

    void failure(User user, boolean newDevice, AuthMedium authMethod, LoginContext context,
            RequestMetadata meta);

    void success(User user, boolean newDevice, AuthMedium authMethod, LoginContext context,
            RequestMetadata meta);
}
