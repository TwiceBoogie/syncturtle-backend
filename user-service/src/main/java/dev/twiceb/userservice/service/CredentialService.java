package dev.twiceb.userservice.service;

import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.userservice.application.internal.params.AuthSubjectParams;
import dev.twiceb.userservice.domain.model.User;

public interface CredentialService {
    User completeLoginOrSignup(String code, AuthSubjectParams subject, AuthMedium provider);
}
