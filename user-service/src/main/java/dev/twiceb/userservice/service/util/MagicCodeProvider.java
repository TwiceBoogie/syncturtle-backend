package dev.twiceb.userservice.service.util;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.util.StringHelper;
import dev.twiceb.userservice.application.internal.params.AuthSubjectParams;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.UserRepository;
import dev.twiceb.userservice.dto.internal.CodeRecord;
import dev.twiceb.userservice.service.AuthInitiator;
import dev.twiceb.userservice.service.AuthProvider;
import dev.twiceb.userservice.service.CredentialService;
import dev.twiceb.userservice.service.FeatureFlagService;
import dev.twiceb.userservice.service.security.HmacHasher;
import dev.twiceb.userservice.utils.MagicCodeGenerator;
import dev.twiceb.userservice.utils.TokenGenerator.TokenPair;
import lombok.RequiredArgsConstructor;

@Component("magicCodeProvider")
@RequiredArgsConstructor
public class MagicCodeProvider implements AuthProvider, AuthInitiator {

    // service
    private final CredentialService credentialService;
    private final FeatureFlagService featureFlagService;
    private final HmacHasher hasher;
    // repository
    private final UserRepository userRepository;
    private final RedisTemplate<String, CodeRecord> redis;

    private static final Duration MAGIC_CODE_EXPIRY = Duration.ofMinutes(10);
    private static final int MAX_ATTEMPTS = 3;

    @Override
    public AuthMedium provider() {
        return AuthMedium.MAGIC_LINK;
    }

    @Override
    public User authenticate(String email, String code, boolean isSignUp) throws AuthException {
        Map<InstanceConfigurationKey, String> configMap = featureFlagService.getConfig();
        String emailHost = configMap.get(InstanceConfigurationKey.EMAIL_HOST);
        boolean isMagicCodeEnabled =
                "1".equals(configMap.get(InstanceConfigurationKey.ENABLE_MAGIC_LINK_LOGIN));

        if (emailHost.isBlank()) {
            throw new AuthException(AuthErrorCodes.SMTP_NOT_CONFIGURED);
        }
        if (!isMagicCodeEnabled) {
            throw new AuthException(AuthErrorCodes.MAGIC_LINK_LOGIN_DISABLED);
        }

        String normEmail = StringHelper.normalizeEmail(email);
        if (!StringHelper.isEmailish(normEmail)) {
            throw new AuthException(AuthErrorCodes.INVALID_EMAIL, Map.of("email", email));
        }

        AuthSubjectParams subject = buildParams(normEmail, code);

        return credentialService.completeLoginOrSignup(code, subject, provider());
    }

    @Override
    public TokenPair initiate(String email) {
        String normEmail = StringHelper.normalizeEmail(email);
        if (!StringHelper.isEmailish(normEmail)) {
            throw new AuthException(AuthErrorCodes.INVALID_EMAIL, Map.of("email", email));
        }

        ValueOperations<String, CodeRecord> ops = redis.opsForValue();
        String token = MagicCodeGenerator.humanReadableToken();
        String redisKey = "magic:" + normEmail;

        // check if redisKey already exist
        CodeRecord record = ops.get(redisKey);
        if (record != null) {
            if (record.getCurrentAttempt() >= MAX_ATTEMPTS) {
                boolean exists = userRepository.existsByEmail(normEmail);
                throw new AuthException(
                        exists ? AuthErrorCodes.EMAIL_CODE_ATTEMPT_EXHAUSTED_SIGN_IN
                                : AuthErrorCodes.EMAIL_CODE_ATTEMPT_EXHAUSTED_SIGN_UP,
                        Map.of("email", normEmail));
            }
            // rotate the token hash and bump current attempt
            record = record.rotate(hasher.hash(token));
        } else {
            // hash new token
            record = CodeRecord.first(normEmail, hasher.hash(token));
        }
        ops.set(redisKey, record, MAGIC_CODE_EXPIRY);

        return new TokenPair(redisKey, token);
    }

    private AuthSubjectParams buildParams(String normalizeEmail, String code) {
        String redisKey = "magic:" + normalizeEmail;
        ValueOperations<String, CodeRecord> ops = redis.opsForValue();
        CodeRecord record = ops.get(redisKey);

        if (record == null) {
            boolean exists = userRepository.existsByEmail(normalizeEmail);
            throw new AuthException(
                    exists ? AuthErrorCodes.EXPIRED_MAGIC_CODE_SIGN_IN
                            : AuthErrorCodes.EXPIRED_MAGIC_CODE_SIGN_UP,
                    Map.of("email", normalizeEmail));
        }

        if (hasher.matches(code, record.getTokenHash())) {
            redis.delete(redisKey);
            return AuthSubjectParams.forPasswordless(normalizeEmail);
        }

        boolean exists = userRepository.existsByEmail(normalizeEmail);
        throw new AuthException(
                exists ? AuthErrorCodes.INVALID_EMAIL_MAGIC_SIGN_IN
                        : AuthErrorCodes.INVALID_EMAIL_MAGIC_SIGN_UP,
                Map.of("email", normalizeEmail));
    }
}
