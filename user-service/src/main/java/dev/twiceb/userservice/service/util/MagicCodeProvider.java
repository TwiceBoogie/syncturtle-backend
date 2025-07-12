package dev.twiceb.userservice.service.util;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.MagicCodeType;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.UserRepository;
import dev.twiceb.userservice.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MagicCodeProvider {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    // @Value("${auth.magic-link.enabled:true}")
    // private boolean magicLinkEnabled;

    // @Value("${spring.mail.host}")
    // private String smtpHost;

    private static final Duration MAGIC_CODE_EXPIRY = Duration.ofMinutes(10);
    private static final int MAX_ATTEMPTS = 3;

    public Pair<String, String> initiate(String email, MagicCodeType type) {
        // if (smtpHost == null || smtpHost.isEmpty()) {
        // throw new AuthException(
        // "SMTP_NOT_CONFIGURED",
        // "SMTP is not configured",
        // email);
        // }

        // if (!magicLinkEnabled) {
        // throw new AuthException(
        // "MAGIC_LINK_LOGIN_DISABLED",
        // "Magic link login is disabled", email);
        // }

        String token = this.generateToken();
        String redisKey = type.buildRedisKey(email);

        Map<String, Object> existingData =
                (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);

        int currentAttempt = 0;
        if (existingData != null) {
            currentAttempt = (int) existingData.get("current_attempt") + 1;
            if (currentAttempt >= MAX_ATTEMPTS) {
                boolean userExists = userRepository.existsByEmail(email);
                switch (type) {
                    case MAGIC_LINK:
                        throw new AuthException(
                                userExists ? AuthErrorCodes.EMAIL_CODE_ATTEMPT_EXHAUSTED_SIGN_IN
                                        : AuthErrorCodes.EMAIL_CODE_ATTEMPT_EXHAUSTED_SIGN_UP);
                    case DEVICE_VERIFICATION:
                        throw new AuthException(
                                AuthErrorCodes.DEVICE_CODE_ATTEMPT_EXHAUSTED_VERIFICATION);
                    default:
                        throw new IllegalStateException("Unexpected MagicCodeType: " + type);
                }
            }
        }

        Map<String, Object> value = new HashMap<>();
        value.put("email", email);
        value.put("token", token);
        value.put("current_attempt", currentAttempt);

        redisTemplate.opsForValue().set(redisKey, value, MAGIC_CODE_EXPIRY);
        return Pair.of(redisKey, token);
    }

    public String validateAndGetEmail(String input, String magicCode, MagicCodeType type,
            String ipAdress) {
        String redisKey = type.buildRedisKey(input);
        Map<String, Object> data = (Map<String, Object>) redisTemplate.opsForValue().get(redisKey);
        if (data == null) {
            // should I log login attempts?
            return this.throwExpired(input, type);
        }

        String storedToken = (String) data.get("token");
        String email = (String) data.get("email");

        if (storedToken.equals(magicCode)) {
            redisTemplate.delete(redisKey);
            return email;
        } else {
            boolean userExists = userRepository.existsByEmail(email);
            if (userExists && type == MagicCodeType.MAGIC_LINK) {
                User user = userRepository.getUserByEmail(email, User.class).orElse(null);
                if (user != null) {
                    loginAttemptService.generateLoginAttempt(false, false, user, ipAdress);
                }
            }
            switch (type) {
                case MAGIC_LINK:
                    throw new AuthException(userExists ? AuthErrorCodes.INVALID_MAGIC_CODE_SIGN_IN
                            : AuthErrorCodes.INVALID_EMAIL_MAGIC_SIGN_UP);
                case DEVICE_VERIFICATION:
                    throw new AuthException(AuthErrorCodes.INVALID_MAGIC_CODE_DEVICE_VERIFICATION);
                default:
                    throw new IllegalStateException("Unexpected MagicCodeType: " + type);
            }
        }
    }

    // if return type is null then this code 'String storedToken = (String)
    // data.get("token");'
    // says it might be a null pointer
    private String throwExpired(String email, MagicCodeType type) {
        boolean userExists = userRepository.existsByEmail(email);
        switch (type) {
            case MAGIC_LINK:
                throw new AuthException(userExists ? AuthErrorCodes.EXPIRED_MAGIC_CODE_SIGN_IN
                        : AuthErrorCodes.EXPIRED_MAGIC_CODE_SIGN_UP);
            case DEVICE_VERIFICATION:
                throw new AuthException(AuthErrorCodes.EXPIRED_MAGIC_CODE_DEVICE);
            default:
                throw new IllegalStateException("Unexpected MagicCodeType: " + type);
        }
    }

    private String generateToken() {
        StringBuilder token = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            if (i > 0)
                token.append("-");
            token.append(randomAlpha(4));
        }
        return token.toString();
    }

    private String randomAlpha(int count) {
        StringBuilder result = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < count; i++) {
            char c = (char) ('a' + random.nextInt(26));
            result.append(c);
        }
        return result.toString();
    }
}
