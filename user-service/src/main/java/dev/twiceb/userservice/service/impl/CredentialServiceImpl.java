package dev.twiceb.userservice.service.impl;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.userservice.domain.model.LoginPolicy;
import dev.twiceb.userservice.domain.model.Profile;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.LoginPolicyRepository;
import dev.twiceb.userservice.domain.repository.ProfileRepository;
import dev.twiceb.userservice.domain.repository.UserRepository;
import dev.twiceb.userservice.dto.internal.AuthUserData;
import dev.twiceb.userservice.service.CredentialService;
import dev.twiceb.userservice.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    // service
    private final FeatureFlagService featureFlagService;
    // repositories
    private final UserRepository userRepository;
    private final LoginPolicyRepository lPolicyRepository;
    private final ProfileRepository profileRepository;

    @Override
    @Transactional
    public User completeLoginOrSignup(String code, AuthUserData authUserData, String provider) {
        String email = authUserData.getEmail();
        String normalizedEmail = normalizeEmail(email);

        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        // used in the callback but not java idiomatic
        // boolean isSignup = user == null;

        if (user == null) {
            // new user
            checkSignUp(normalizedEmail);

            // initalize user
            user = new User(normalizedEmail, UUID.randomUUID().toString().replace("-", ""));

            // check if password is autoset
            if (authUserData.getUserData().isPasswordAutoset()) {
                user.setPassword(UUID.randomUUID().toString().replace("-", ""));
                user.setPasswordAutoSet(true);
                user.setEmailVerified(true);
            } else {
                validatePassword(normalizedEmail, code);
                user.setPassword(code);
                user.setPasswordAutoSet(false);
            }

            // set user details
            LoginPolicy policyRef = lPolicyRepository.getReferenceById(1L); // default
            user.setLoginPolicy(policyRef);
            String firstName = authUserData.getUserData().getFirstName();
            String lastName = authUserData.getUserData().getLastName();
            user.setFirstName(firstName);
            user.setLastName(lastName);
            // user must exist for it to be referenced by Profile
            user = userRepository.save(user);

            // create default
            Profile profile = Profile.create(user, null);
            profileRepository.save(profile);
        }

        user = touchUserLoginSnapshot(provider, user);

        return user;
    }

    private void validatePassword(String normalizedEmail, String code) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validatePassword'");
    }

    private String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new AuthException(AuthErrorCodes.INVALID_EMAIL, Map.of("email", email));
        }
        return email.strip().toLowerCase();
    }

    private boolean checkSignUp(String email) {
        String signupEnabled = featureFlagService.get(InstanceConfigurationKey.ENABLE_SIGNUP);

        if ("0".equals(signupEnabled)) {
            throw new AuthException(AuthErrorCodes.SIGNUP_DISABLED, Map.of("email", email));
        }

        return true;
    }

    private User touchUserLoginSnapshot(String provider, User user) {
        user.setLastLoginMedium(provider);
        user.setLastActive(Instant.now());
        user.setLastLoginTime(Instant.now());
        // next 2 are done in the LoginAttempt
        // user.setLastLoginIp("random");
        // user.setLastLoginUagent("useragent");
        if (!user.isActive()) {
            // send activation email and set user as active
            // user_activation_email.delay(base_host(request=self.request), user.id)
        }
        // set user as active
        user.setActive(true);

        return userRepository.save(user);
    }

}
