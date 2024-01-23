package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.security.JwtProvider;
import dev.twiceb.userservice.amqp.AmqpPublisher;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.ProcessEmailRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.model.ActivationCode;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.ActivationCodeRepository;
import dev.twiceb.userservice.repository.UserRepository;
import dev.twiceb.userservice.repository.projection.AuthUserProjection;
import dev.twiceb.userservice.repository.projection.UserPrincipalProjection;
import dev.twiceb.userservice.service.RegistrationService;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.Map;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final UserServiceHelper userServiceHelper;
    private final ActivationCodeRepository activationCodeRepository;
    private final JwtProvider jwtProvider;
    private final AmqpPublisher amqpPublisher;

    @Override
    @Transactional
    public Map<String, String> registration(RegistrationRequest request, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult).processPassword(
                request.getPassword(), request.getPasswordConfirm()
        );

        if (!userRepository.isUserExistByEmail(request.getEmail())) {
            User user = userServiceHelper.createUserEntity(request);
            userRepository.save(user);
            return Map.of("message", "Account created successfully");
        }
        throw new ApiRequestException(EMAIL_HAS_ALREADY_BEEN_TAKEN, HttpStatus.BAD_REQUEST);
    }

    @Override
    @Transactional
    public Map<String, String> sendRegistrationCode(ProcessEmailRequest request, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        User user = userRepository.getUserByEmail(request.getEmail(), User.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        if (user.isVerified()) {
            throw new ApiRequestException(ACCOUNT_ALREADY_VERIFIED, HttpStatus.BAD_REQUEST);
        }
        // either on service or api-gateway implement feature to deal with multiple
        // request to this function, in order to not generate multiple codes for a
        // single user.
        ActivationCode code = userServiceHelper.createActivationCodeEntity(user);
        activationCodeRepository.save(code);

        EmailRequest emailRequest = new EmailRequest.Builder(
                user.getEmail(), "Registration Code", "registration-template").attributes(
                        Map.of(
                                "fullName", user.getFirstName() + " " + user.getLastName(),
                                "registrationCode", code.getHashedCode()))
                .build();

        amqpPublisher.sendEmail(emailRequest);

        return Map.of("message", "Activation Code was sent to your email.");
    }

    @Override
    @Transactional
    public Map<String, String> checkRegistrationCode(String code) {
        ActivationCode ac = activationCodeRepository.getActivationCodeByHashedCode(code, ActivationCode.class)
                .orElseThrow(() -> new ApiRequestException(ACTIVATION_CODE_NOT_FOUND, HttpStatus.BAD_REQUEST));

        userRepository.save(userServiceHelper.processUser(ac.getUser(), ac.getExpirationTime()));

        UserPrincipalProjection updatedUser = userRepository
                .getUserByEmail(ac.getUser().getEmail(), UserPrincipalProjection.class)
                .orElseThrow(() -> new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));
        // if error = rollback changes to database
        amqpPublisher.userCreated(updatedUser);

        return Map.of("message", "Activation Successful! Please log in again to access your account.");
    }

    @Override
    @Transactional
    @Deprecated
    public Map<String, Object> endRegistration(AuthenticationRequest request, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        AuthUserProjection user = userRepository.getUserByEmail(request.getEmail(), AuthUserProjection.class)
                .orElseThrow(
                        () -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));

        userRepository.updateActiveUserProfile(user.getId());
        userRepository.updatePassword(userServiceHelper.encodePassword(request.getPassword()), user.getId());

        String token = jwtProvider.createToken(request.getEmail(), "USER");

        return Map.of(
                "user", user,
                "token", token);
    }
}
