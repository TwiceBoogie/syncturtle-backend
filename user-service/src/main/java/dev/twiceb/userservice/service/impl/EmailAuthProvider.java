package dev.twiceb.userservice.service.impl;

import java.util.Map;
import org.springframework.stereotype.Component;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.exception.AuthException;
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
    public String provider() {
        return "Email";
    }

    @Override
    public User authenticate(String email, String password, boolean isSignUp) throws AuthException {
        boolean isEmailPWEnabled =
                "1".equals(featureFlagService.get(InstanceConfigurationKey.ENABLE_EMAIL_PASSWORD));

        if (!isEmailPWEnabled) {
            throw new AuthException(AuthErrorCodes.EMAIL_PASSWORD_AUTHENTICATION_DISABLED);
        }

        AuthSubjectParams subject = buildParams(email, password, isSignUp);

        return credentialService.completeLoginOrSignup(password, subject, provider());
    }

    private AuthSubjectParams buildParams(String email, String password, boolean isSignUp) {
        if (isSignUp) {
            if (userRepository.existsByEmail(email)) {
                throw new AuthException(AuthErrorCodes.USER_DOES_NOT_EXIST, Map.of("email", email));
            }

            return AuthSubjectParams.forEmailPassword(email);
        } else {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthException(AuthErrorCodes.USER_ALREADY_EXIST));

            if (!hasher.matches(password, user.getPassword())) {
                AuthErrorCodes errCode =
                        isSignUp == true ? AuthErrorCodes.AUTHENTICATION_FAILED_SIGN_UP
                                : AuthErrorCodes.AUTHENTICATION_FAILED_SIGN_IN;
                throw new AuthException(errCode, Map.of("email", email));
            }

            return AuthSubjectParams.forEmailPassword(email);
        }
    }

}
