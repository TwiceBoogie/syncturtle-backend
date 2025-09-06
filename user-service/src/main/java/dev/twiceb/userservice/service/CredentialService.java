package dev.twiceb.userservice.service;

import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.dto.internal.AuthUserData;

public interface CredentialService {
    User completeLoginOrSignup(String code, AuthUserData userData, String provider);
}
