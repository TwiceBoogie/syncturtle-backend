package dev.twiceb.userservice.service;

import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.response.AdminTokenGrant;
import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.userservice.dto.request.*;

public interface RegistrationService {
    TokenGrant magicSignup(AuthContextRequest<MagicCodeRequest> request);

    TokenGrant signup(AuthContextRequest<RegistrationRequest> request);

    AdminTokenGrant adminSignup(AuthContextRequest<AdminSignupRequest> request);
}
