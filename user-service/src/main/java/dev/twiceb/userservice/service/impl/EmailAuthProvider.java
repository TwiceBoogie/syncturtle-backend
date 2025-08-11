package dev.twiceb.userservice.service.impl;

import org.springframework.stereotype.Component;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.userservice.Credentials;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.service.AuthProvider;
import dev.twiceb.userservice.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailAuthProvider implements AuthProvider {

    private final FeatureFlagService featureFlagService;

    @Override
    public String provider() {
        return "Email";
    }

    @Override
    public User authenticate(Credentials creds, boolean isSignUp) throws AuthException {
        boolean isEmailPWEnabled = "1".equals(featureFlagService.get("ENABLE_EMAIL_PASSWORD"));

        if (!isEmailPWEnabled) {
            throw new AuthException(AuthErrorCodes.EMAIL_PASSWORD_AUTHENTICATION_DISABLED);
        }


        throw new UnsupportedOperationException("Unimplemented method 'authenticate'");
    }

}
