package dev.twiceb.userservice.service.impl;

import java.util.Map;
import org.springframework.stereotype.Component;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.UserRepository;
import dev.twiceb.userservice.dto.internal.AuthUserData;
import dev.twiceb.userservice.service.AuthProvider;
import dev.twiceb.userservice.service.CredentialService;
import dev.twiceb.userservice.service.FeatureFlagService;
import dev.twiceb.userservice.service.security.BcryptHasher;
import lombok.RequiredArgsConstructor;

@Component("emailAuthProvider")
@RequiredArgsConstructor
public class EmailAuthProvider implements AuthProvider {

    private final FeatureFlagService featureFlagService;
    private final CredentialService credentialService;
    private final UserRepository userRepository;
    private final BcryptHasher hasher;

    @Override
    public String provider() {
        return "Email";
    }

    @Override
    public User authenticate(String key, String code, boolean isSignUp) throws AuthException {
        boolean isEmailPWEnabled =
                "1".equals(featureFlagService.get(InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD));

        if (!isEmailPWEnabled) {
            throw new AuthException(AuthErrorCodes.EMAIL_PASSWORD_AUTHENTICATION_DISABLED);
        }

        AuthUserData userData = setUserData(key, code, isSignUp);

        return credentialService.completeLoginOrSignup(code, userData, provider());
    }

    private AuthUserData setUserData(String key, String code, boolean isSignUp) {
        if (isSignUp) {
            if (userRepository.existsByEmail(key)) {
                throw new AuthException(AuthErrorCodes.USER_DOES_NOT_EXIST, Map.of("email", key));
            }

            return AuthUserData.forEmailPassword(key);
        } else {
            User user = userRepository.findByEmail(key)
                    .orElseThrow(() -> new AuthException(AuthErrorCodes.USER_ALREADY_EXIST));

            if (!hasher.matches(code, user.getPassword())) {
                AuthErrorCodes errCode =
                        isSignUp == true ? AuthErrorCodes.AUTHENTICATION_FAILED_SIGN_UP
                                : AuthErrorCodes.AUTHENTICATION_FAILED_SIGN_IN;
                throw new AuthException(errCode, Map.of("email", key));
            }

            return AuthUserData.forEmailPassword(key);
        }
    }

}
