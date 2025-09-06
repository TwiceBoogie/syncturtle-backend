package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.context.AuthContext;
import dev.twiceb.common.dto.context.RequestMetadataContext;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.enums.MagicCodeType;
import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.records.AuthenticatedUserRecord;
import dev.twiceb.common.records.MagicCodeRecord;
import dev.twiceb.userservice.domain.enums.LoginContext;
import dev.twiceb.userservice.domain.model.*;
import dev.twiceb.userservice.domain.projection.UserPrincipalProjection;
import dev.twiceb.userservice.domain.repository.*;
import dev.twiceb.userservice.dto.internal.TokenProvenance;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.RefreshTokenRequest;
import dev.twiceb.userservice.service.*;
import dev.twiceb.userservice.service.security.BcryptHasher;
import dev.twiceb.userservice.utils.TokenGenerator.TokenPair;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.*;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    // services
    private final FeatureFlagService featureFlagService;
    private final LoginService loginService;
    private final TokenService tokenService;
    private final EmailService emailService;
    // providers
    private final Map<String, AuthInitiator> initiateAuthByBeanName;
    private final Map<String, AuthProvider> verifyAuthByBeanName;
    private final BcryptHasher hasher;
    // repository
    private final UserRepository userRepository;
    private final LoginPolicyRepository lPolicyRepository;
    private final ProfileRepository profileRepository;

    @Override
    public UUID getAuthenticatedUserId() {
        return AuthContext.get();
    }

    @Override
    public User getAuthenticatedUser() {
        return userRepository.findById(getAuthenticatedUserId())
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public UserPrincipalProjection getUserPrincipleByEmail(String email) {
        return userRepository.getUserByEmail(email, UserPrincipalProjection.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public MagicCodeRecord checkEmail(String email) {
        Map<InstanceConfigurationKey, String> configMap = featureFlagService.getConfig();
        boolean isSmtpConfigured = configMap.get(InstanceConfigurationKey.EMAIL_HOST).isBlank();
        boolean isMagicCodeEnabled =
                "1".equals(configMap.get(InstanceConfigurationKey.ENABLE_MAGIC_LINK_LOGIN));

        String status = (isSmtpConfigured && isMagicCodeEnabled) ? "MAGIC_CODE" : "CREDENTIAL";

        String normalizedEmail = email.trim().toLowerCase();
        // check if email-service is up (ping)
        // check if user already exists with provided email
        boolean isUserExist = userRepository.existsByEmail(normalizedEmail);
        if (isUserExist) {
            return new MagicCodeRecord(true, status);
        }

        return new MagicCodeRecord(false, status);
    }

    @Override
    public String generateMagicCode(String email, MagicCodeType type) {
        AuthInitiator provider = initiateAuthByBeanName.get("magicCodeProvider");
        String normalizedEmail = email.trim().toLowerCase();

        // handle = redis key; secret = token
        TokenPair pair = provider.initiate(normalizedEmail);
        emailService.sendMagicCodeEmail(email, pair.secret());
        return pair.handle();
    }

    @Override
    @Transactional
    public TokenGrant magicLogin(AuthContextRequest<MagicCodeRequest> request) {
        AuthProvider provider = verifyAuthByBeanName.get("emailAuthProvider");

        MagicCodeRequest payload = request.getPayload();
        RequestMetadata metadata = request.getMetadata();
        String email = payload.getEmail().trim().toLowerCase();

        boolean isUserExist = userRepository.existsByEmail(email);

        if (!isUserExist) {
            throw new AuthException(AuthErrorCodes.USER_DOES_NOT_EXIST);
        }

        // 0: prefetch user for lock check
        // Optional<User> userOpt = userRepo.findByEmail(email);
        // if (userOpt.isPresent() && userOpt.get().getStatus() == UserStatus.LOCKED) {
        // hasher.fakeVerify(req.getPassword()); // constant-time work to avoid timing oracle
        // loginAttempts.recordFailure(userOpt.get().getId(), email, meta, LoginContext.APP,
        // "PASSWORD", "LOCKED", /* deviceId */ null);
        // throw new AuthException(AuthErrorCodes.ACCOUNT_LOCKED);
        // }

        try {
            // 1: auth via provider; created/updated user from db
            User user = provider.authenticate("magic_" + email, payload.getMagicCode(), false);
            // get or create Profile
            Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);

            if (user.isPasswordAutoSet() && profile.isOnboarded()) {
                // path = "accounts/set-password";
            }
            // 2: lock check (optional if you do it pre-auth)
            // policyService.ensureNotLocked(user.getId(), metadata.getIpAddress());
            // 3: device upsert

            // 4: mintt tokens w/ provenance
            TokenProvenance provenance = TokenProvenance.builder().ip(metadata.getIpAddress())
                    .userAgent(metadata.getUserAgent()).domain(metadata.getDomain())
                    .context(LoginContext.APP)
                    // .deviceId(device.getId())
                    .requestId(metadata.getRequestId()).correlationId(metadata.getCorrelationId())
                    .now(Instant.now()).build();

            return tokenService.mint(user, provenance);
        } catch (AuthException e) {
            // log login attempt failure and rethrow
            throw e;
        }
    }

    @Override
    @Transactional
    public TokenGrant refreshToken(AuthContextRequest<RefreshTokenRequest> request) {
        RefreshTokenRequest payload = request.getPayload();
        RequestMetadata metadata = request.getMetadata();

        TokenProvenance provenance = TokenProvenance.builder().ip(metadata.getIpAddress())
                .userAgent(metadata.getUserAgent()).domain(metadata.getDomain())
                .context(LoginContext.APP)
                // .deviceId(device.getId())
                .requestId(metadata.getRequestId()).correlationId(metadata.getCorrelationId())
                .now(Instant.now()).build();
        return tokenService.rotate(payload.getToken(), provenance);
    }

    @Override
    @Transactional
    public TokenGrant login(AuthContextRequest<AuthenticationRequest> request) {
        // get provider
        AuthProvider provider = verifyAuthByBeanName.get("emailAuthProvider");

        AuthenticationRequest payload = request.getPayload();
        RequestMetadata meta = request.getMetadata();
        String email = payload.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            throw new AuthException(AuthErrorCodes.USER_DOES_NOT_EXIST);
        }

        if (user.getUserStatus().equals(UserStatus.LOCKED)) {
            // hasher.fakeVerify(payload.getPassword()); //constant-time work to avoid timing oracle
            loginService.failure(user, false, AuthMedium.PASSWORD, LoginContext.APP, meta);
        }

        try {
            // 1: auth via provider; created/updated user from db
            user = provider.authenticate(email, payload.getPassword(), false);
            // get or create Profile
            Profile profile = profileRepository.findByUserId(user.getId()).orElse(null);

            if (user.isPasswordAutoSet() && profile.isOnboarded()) {
                // path = "accounts/set-password";
            }
            // 2: lock check (optional if you do it pre-auth)
            // policyService.ensureNotLocked(user.getId(), metadata.getIpAddress());
            // 3: device upsert

            // 4: mintt tokens w/ provenance
            TokenProvenance provenance =
                    TokenProvenance.builder().ip(meta.getIpAddress()).userAgent(meta.getUserAgent())
                            .domain(meta.getDomain()).context(LoginContext.APP)
                            // .deviceId(device.getId())
                            .requestId(meta.getRequestId()).correlationId(meta.getCorrelationId())
                            .now(Instant.now()).build();

            return tokenService.mint(user, provenance);
        } catch (AuthException e) {
            // log login attempt failure and rethrow
            loginService.failure(user, false, null, null, meta);
            throw e;
        }
    }

    @Override
    public AuthenticatedUserRecord getUserByToken() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getUserByToken'");
    }

    @Override
    @Transactional
    public TokenGrant createAdminUser(AdminSignupRequest payload) {
        // zxcbn here
        // normalize email
        String email = payload.getEmail().trim().toLowerCase();
        LoginPolicy policyRef = lPolicyRepository.getReferenceById(1L);
        User user = User.createWithPasswordAdmin(email, hasher.hash(payload.getPassword()),
                policyRef, payload.getFirstName(), payload.getLastName());
        user = userRepository.save(user);
        Profile profile = Profile.create(user, payload.getCompanyName());
        profileRepository.save(profile);
        // either grab from the Context or pass it thorugh as an argument;
        RequestMetadata meta = RequestMetadataContext.get();
        TokenProvenance provenance = TokenProvenance.builder().ip(meta.getIpAddress())
                .userAgent(meta.getUserAgent()).domain(meta.getDomain()).context(LoginContext.APP)
                // .deviceId(device.getId())
                .requestId(meta.getRequestId()).correlationId(meta.getCorrelationId())
                .now(Instant.now()).build();

        return tokenService.mint(user, provenance);
    }
}
