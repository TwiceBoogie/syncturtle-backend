package dev.twiceb.userservice.service.impl;

import java.time.Instant;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.event.UserEvent;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.util.StringHelper;
import dev.twiceb.userservice.application.internal.params.AuthSubjectParams;
import dev.twiceb.userservice.application.internal.params.RegistrationDraft;
import dev.twiceb.userservice.domain.model.LoginPolicy;
import dev.twiceb.userservice.domain.model.Profile;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.LoginPolicyRepository;
import dev.twiceb.userservice.domain.repository.ProfileRepository;
import dev.twiceb.userservice.domain.repository.UserRepository;
// import dev.twiceb.userservice.events.UserAuthenticatedEvent;
import dev.twiceb.userservice.events.UserChangedEvent;
import dev.twiceb.userservice.service.CredentialService;
import dev.twiceb.userservice.service.FeatureFlagService;
import dev.twiceb.userservice.service.security.BcryptHasher;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    // service
    private final FeatureFlagService featureFlagService;
    // publisher
    private final ApplicationEventPublisher publisher;
    // repositories
    private final UserRepository userRepository;
    private final LoginPolicyRepository lPolicyRepository;
    private final ProfileRepository profileRepository;
    // hasher
    private final BcryptHasher hasher;

    @Override
    @Transactional
    public User completeLoginOrSignup(String codeOrPassword, AuthSubjectParams subject,
            AuthMedium provider) {
        String normalizedEmail = StringHelper.normalizeEmail(subject.getEmail());
        if (!StringHelper.isEmailish(normalizedEmail)) {
            throw new AuthException(AuthErrorCodes.INVALID_EMAIL,
                    Map.of("email", subject.getEmail()));
        }

        User user = userRepository.findByEmail(normalizedEmail).orElse(null);
        // used in the callback but not java idiomatic
        // boolean isSignup = (user == null);
        // its a signup
        if (user == null) {
            // new user
            ensureSignupEnabled(normalizedEmail);

            LoginPolicy policyRef = lPolicyRepository.getReferenceById(1L);
            RegistrationDraft draft = subject.getDraft();

            // check if password is autoset
            if (draft != null && draft.isPasswordAutoset()) {
                user = User.createPasswordless(normalizedEmail, policyRef);

                if (!StringHelper.isBlank(draft.getFirstName())
                        || !StringHelper.isBlank(draft.getLastName())) {
                    // user.completeOnboarding(StringHelper.nvl(draft.getFirstName(), ""),
                    // StringHelper.nvl(draft.getLastName(), ""), null, null);
                }
            } else {
                validatePasswordPolicy(normalizedEmail, codeOrPassword);
                String hash = hasher.hash(codeOrPassword);
                user = User.createWithPassword(normalizedEmail, hash, policyRef);
            }

            // user must exist for it to be referenced by Profile
            user = userRepository.save(user);

            publisher.publishEvent(new UserChangedEvent(UserEvent.Type.USER_CREATED, user.getId(),
                    user.getEmail(), user.getFirstName(), user.getLastName(), user.getDisplayName(),
                    user.getDateJoined(), Instant.now(), user.getVersion()));

            // create default
            Profile profile = Profile.create(user, null);
            profileRepository.save(profile);
        }

        boolean shouldEmail = false;
        if (!user.isActive()) {
            shouldEmail = true;
        }
        user.markLastLogin(provider, Instant.now());
        user.setActive(true);

        user = userRepository.save(user);

        if (shouldEmail) {
            // publisher.publishEvent(new UserAuthenticatedEvent(user.getId(), isSignup, provider,
            // user.getEmail(), Instant.now()));
        }

        return user;
    }

    private void validatePasswordPolicy(String email, String rawPassword) {
        // TODO consult loginPoilicy (min length, compleixy, breach pw check, etc)
        if (StringHelper.isBlank(rawPassword)) {
            throw new AuthException(AuthErrorCodes.INVALID_PASSWORD, Map.of("email", email));
        }
    }

    private boolean ensureSignupEnabled(String email) {
        String signupEnabled = featureFlagService.get(InstanceConfigurationKey.ENABLE_SIGNUP);

        if ("0".equals(signupEnabled)) {
            throw new AuthException(AuthErrorCodes.SIGNUP_DISABLED, Map.of("email", email));
        }

        return true;
    }

}
