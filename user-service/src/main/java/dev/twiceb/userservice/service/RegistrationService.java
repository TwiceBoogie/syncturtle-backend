package dev.twiceb.userservice.service;

import dev.twiceb.common.dto.internal.AuthAdminResult;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.userservice.dto.internal.AuthUserResult;
import dev.twiceb.userservice.dto.request.*;

public interface RegistrationService {
    AuthUserResult magicSignup(AuthContextRequest<MagicCodeRequest> request);

    AuthUserResult signup(AuthContextRequest<RegistrationRequest> request);

    AuthAdminResult adminSignup(AuthContextRequest<AdminSignupRequest> request);
}
