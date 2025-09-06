package dev.twiceb.userservice.service;

import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.common.enums.MagicCodeType;
import dev.twiceb.common.records.AuthenticatedUserRecord;
import dev.twiceb.common.records.MagicCodeRecord;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.projection.UserPrincipalProjection;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.RefreshTokenRequest;
import java.util.UUID;

public interface AuthenticationService {

    UUID getAuthenticatedUserId();

    User getAuthenticatedUser();

    UserPrincipalProjection getUserPrincipleByEmail(String email);

    MagicCodeRecord checkEmail(String email);

    String generateMagicCode(String email, MagicCodeType type);

    TokenGrant magicLogin(AuthContextRequest<MagicCodeRequest> request);

    TokenGrant login(AuthContextRequest<AuthenticationRequest> request);

    AuthenticatedUserRecord getUserByToken();

    TokenGrant refreshToken(AuthContextRequest<RefreshTokenRequest> request);

    TokenGrant createAdminUser(AdminSignupRequest payload);
}
