package dev.twiceb.userservice.service.impl;

import java.util.Map;
import org.springframework.stereotype.Component;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.util.StringHelper;
import dev.twiceb.userservice.application.internal.params.AuthSubjectParams;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.UserRepository;
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
    public AuthMedium provider() {
        return AuthMedium.PASSWORD;
    }

    @Override
    public User authenticate(String email, String password, boolean isSignUp) throws AuthException {
        boolean isEmailPWEnabled =
                "1".equals(featureFlagService.get(InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD));

        if (!isEmailPWEnabled) {
            throw new AuthException(AuthErrorCodes.EMAIL_PASSWORD_AUTHENTICATION_DISABLED);
        }

        String normEmail = StringHelper.normalizeEmail(email);
        if (!StringHelper.isEmailish(normEmail)) {
            throw new AuthException(AuthErrorCodes.INVALID_EMAIL, Map.of("email", email));
        }

        AuthSubjectParams subject = buildParams(normEmail, password, isSignUp);

        return credentialService.completeLoginOrSignup(password, subject, provider());
    }

    private AuthSubjectParams buildParams(String normalizedEmail, String password,
            boolean isSignUp) {
        if (isSignUp) {
            if (userRepository.existsByEmail(normalizedEmail)) {
                throw new AuthException(AuthErrorCodes.USER_ALREADY_EXIST,
                        Map.of("email", normalizedEmail));
            }

            return AuthSubjectParams.forEmailPassword(normalizedEmail);
        } else {
            User user = userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new AuthException(AuthErrorCodes.USER_DOES_NOT_EXIST,
                            Map.of("email", normalizedEmail)));

            if (!hasher.matches(password, user.getPassword())) {
                throw new AuthException(AuthErrorCodes.AUTHENTICATION_FAILED_SIGN_IN,
                        Map.of("email", normalizedEmail));
            }

            return AuthSubjectParams.forEmailPassword(normalizedEmail);
        }
    }

}
