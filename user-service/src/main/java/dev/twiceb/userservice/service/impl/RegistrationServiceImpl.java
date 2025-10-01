package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.application.internal.bundle.IssuedTokens;
import dev.twiceb.common.dto.internal.AuthAdminResult;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.userservice.domain.enums.LoginContext;
import dev.twiceb.userservice.domain.model.LoginPolicy;
import dev.twiceb.userservice.domain.model.Profile;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.LoginPolicyRepository;
import dev.twiceb.userservice.domain.repository.ProfileRepository;
import dev.twiceb.userservice.domain.repository.UserRepository;
import dev.twiceb.userservice.dto.internal.AuthUserResult;
import dev.twiceb.userservice.dto.internal.TokenProvenance;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.service.AuthProvider;
import dev.twiceb.userservice.service.LoginService;
import dev.twiceb.userservice.service.RegistrationService;
import dev.twiceb.userservice.service.TokenService;
import dev.twiceb.userservice.service.security.BcryptHasher;
import dev.twiceb.userservice.service.util.RedirectionPathHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    // service
    private final TokenService tokenService;
    private final LoginService loginService;
    // helpers
    private final RedirectionPathHelper redirectionPathHelper;
    // repositories
    private final UserRepository userRepository;
    private final LoginPolicyRepository lPolicyRepository;
    private final ProfileRepository profileRepository;
    // auth providers
    private final Map<String, AuthProvider> verifyAuthByBeanName;
    private final BcryptHasher hasher;

    @Override // orchestrator
    @Transactional
    public AuthUserResult signup(AuthContextRequest<RegistrationRequest> request) {
        AuthProvider provider = verifyAuthByBeanName.get("emailAuthProvider");

        RegistrationRequest payload = request.getPayload();
        RequestMetadata metadata = request.getMetadata();

        try {
            // 1: auth via provider; created/updated user from db
            User user = provider.authenticate(payload.getEmail(), payload.getPassword(), true);

            // 2: lock check (optional if you do it pre-auth)
            // policyService.ensureNotLocked(user.getId(), metadata.getIpAddress());
            // 3: device upsert

            // 4: log user and ?store device info?
            loginService.success(user, true, AuthMedium.PASSWORD, LoginContext.APP, metadata);

            // 5: mintt tokens w/ provenance and create refresh/access token
            TokenProvenance provenance = TokenProvenance.builder().ip(metadata.getIpAddress())
                    .userAgent(metadata.getUserAgent()).domain(metadata.getDomain())
                    .context(LoginContext.APP)
                    // .deviceId(device.getId())
                    .requestId(metadata.getRequestId()).correlationId(metadata.getCorrelationId())
                    .now(Instant.now()).build();
            IssuedTokens issuedTokens = tokenService.issueTokens(user, provenance);

            // get redirection path
            String path = redirectionPathHelper.getRedirectionPath(user);

            return new AuthUserResult(path, issuedTokens);
        } catch (AuthException e) {
            // might do something here
            throw e;
        }
    }

    @Override
    @Transactional
    public AuthUserResult magicSignup(AuthContextRequest<MagicCodeRequest> request) {
        AuthProvider provider = verifyAuthByBeanName.get("magicCodeProvider");

        MagicCodeRequest payload = request.getPayload();
        RequestMetadata metadata = request.getMetadata();
        String normalizedEmail = payload.getEmail().trim().toLowerCase();

        boolean isUserExist = userRepository.existsByEmail(normalizedEmail);
        if (isUserExist) {
            throw new AuthException(AuthErrorCodes.USER_ALREADY_EXIST);
        }

        try {
            // 1: auth via provider; created/updated user from db
            User user = provider.authenticate(normalizedEmail, payload.getMagicCode(), true);

            // 2: lock check (optional if you do it pre-auth)
            // policyService.ensureNotLocked(user.getId(), metadata.getIpAddress());
            // 3: device upsert

            // 4: log user and ?store device info?
            loginService.success(user, isUserExist, AuthMedium.MAGIC_LINK, LoginContext.APP,
                    metadata);

            // 5: mintt tokens w/ provenance and create refresh/access token
            TokenProvenance provenance = TokenProvenance.builder().ip(metadata.getIpAddress())
                    .userAgent(metadata.getUserAgent()).domain(metadata.getDomain())
                    .context(LoginContext.APP)
                    // .deviceId(device.getId())
                    .requestId(metadata.getRequestId()).correlationId(metadata.getCorrelationId())
                    .now(Instant.now()).build();
            IssuedTokens tokenGrant = tokenService.issueTokens(user, provenance);

            // 6: get redirection path
            String path = redirectionPathHelper.getRedirectionPath(user);
            return new AuthUserResult(path, tokenGrant);
        } catch (AuthException e) {
            // might do something here
            throw e;
        }
    }

    @Override
    @Transactional
    public AuthAdminResult adminSignup(AuthContextRequest<AdminSignupRequest> request) {
        AdminSignupRequest payload = request.getPayload();
        RequestMetadata meta = request.getMetadata();

        // normalize email
        String email = payload.getEmail().trim().toLowerCase();
        LoginPolicy policyRef = lPolicyRepository.getReferenceById(1L);
        User user = User.createWithPasswordAdmin(email, hasher.hash(payload.getPassword()),
                policyRef, payload.getFirstName(), payload.getLastName());
        user = userRepository.save(user);
        Profile profile = Profile.create(user, payload.getCompanyName());
        profileRepository.save(profile);

        // 4: log user and ?store device info?
        loginService.success(user, true, AuthMedium.PASSWORD, LoginContext.ADMIN, meta);

        // 5: mintt tokens w/ provenance and create refresh/access token
        TokenProvenance provenance = TokenProvenance.builder().ip(meta.getIpAddress())
                .userAgent(meta.getUserAgent()).domain(meta.getDomain()).context(LoginContext.APP)
                // .deviceId(device.getId())
                .requestId(meta.getRequestId()).correlationId(meta.getCorrelationId())
                .now(Instant.now()).build();
        IssuedTokens tokenGrant = tokenService.issueTokens(user, provenance);
        return new AuthAdminResult(user.getId(), tokenGrant);
    }
}
