package dev.twiceb.userservice.service.util;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.exception.AuthException;
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
    public String provider() {
        return "Magic";
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

        AuthSubjectParams subject = buildParams(email, code);

        return credentialService.completeLoginOrSignup(code, subject, provider());
    }

    @Override
    public TokenPair initiate(String email) {
        ValueOperations<String, CodeRecord> ops = redis.opsForValue();
        String token = MagicCodeGenerator.humanReadableToken();
        String key = "magic_" + email;

        // check if key already exist
        CodeRecord record = ops.get(key);
        int currentAttempt = 0;
        if (record != null) {
            currentAttempt = record.getCurrentAttempt();
            if (currentAttempt >= MAX_ATTEMPTS) {
                if (userRepository.existsByEmail(email)) {
                    throw new AuthException(AuthErrorCodes.EMAIL_CODE_ATTEMPT_EXHAUSTED_SIGN_IN,
                            Map.of("email", email));
                } else {
                    throw new AuthException(AuthErrorCodes.EMAIL_CODE_ATTEMPT_EXHAUSTED_SIGN_UP,
                            Map.of("email", email));
                }
            }
            // hash new token
            String tokenHash = hasher.hash(token);
            // rotate the token hash and bump current attempt
            record = record.rotate(tokenHash);
            ops.set(key, record, MAGIC_CODE_EXPIRY);
        } else {
            // hash new token
            String tokenHash = hasher.hash(token);
            record = CodeRecord.first(email, tokenHash);
            ops.set(key, record, MAGIC_CODE_EXPIRY);
        }

        return new TokenPair(key, token);
    }

    private AuthSubjectParams buildParams(String key, String code) {
        ValueOperations<String, CodeRecord> ops = redis.opsForValue();
        // check if record exist by key
        CodeRecord record = ops.get(key);
        if (record != null) {
            String tokenHash = record.getTokenHash();
            // delete code and return user data
            if (hasher.matches(code, tokenHash)) {
                redis.delete(key);
                return AuthSubjectParams.forPasswordless(record.getEmail());
            } else {
                String email = key.split("_", 2)[1];
                if (userRepository.existsByEmail(email)) {
                    throw new AuthException(AuthErrorCodes.INVALID_EMAIL_MAGIC_SIGN_IN,
                            Map.of("email", email));
                } else {
                    throw new AuthException(AuthErrorCodes.INVALID_EMAIL_MAGIC_SIGN_UP,
                            Map.of("email", email));
                }
            }
        } else {
            String email = key.split("_", 2)[1];
            if (userRepository.existsByEmail(email)) {
                throw new AuthException(AuthErrorCodes.EXPIRED_MAGIC_CODE_SIGN_IN,
                        Map.of("email", email));
            } else {
                throw new AuthException(AuthErrorCodes.EXPIRED_MAGIC_CODE_SIGN_UP,
                        Map.of("email", email));
            }
        }
    }
}
