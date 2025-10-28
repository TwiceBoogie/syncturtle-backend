package dev.twiceb.userservice.service;

import dev.twiceb.common.application.internal.bundle.IssuedTokens;
import dev.twiceb.common.dto.internal.AuthAdminResult;
import dev.twiceb.common.dto.internal.AuthUserResult;
import dev.twiceb.common.dto.internal.MagicCodeResult;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.enums.MagicCodeType;
import dev.twiceb.common.records.AuthenticatedUserRecord;
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

    UUID findUserIdByEmail(String email);

    UserPrincipalProjection findUserByToken(UUID userId);

    UserPrincipalProjection getUserPrincipleByEmail(String email);

    MagicCodeResult checkEmail(String email);

    String generateMagicCode(String email, MagicCodeType type);

    AuthUserResult magicLogin(AuthContextRequest<MagicCodeRequest> request);

    AuthUserResult login(AuthContextRequest<AuthenticationRequest> request);

    AuthenticatedUserRecord getUserByToken();

    IssuedTokens refreshToken(AuthContextRequest<RefreshTokenRequest> request);

    AuthAdminResult createAdminUser(AdminSignupRequest payload);
}
