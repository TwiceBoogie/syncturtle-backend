package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.security.JwtProvider;
import dev.twiceb.userservice.amqp.AmqpPublisher;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.enums.ActivationCodeType;
import dev.twiceb.userservice.model.*;
import dev.twiceb.userservice.repository.*;
import dev.twiceb.userservice.repository.projection.UserPrincipalProjection;
import dev.twiceb.userservice.service.RegistrationService;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final UserServiceHelper userServiceHelper;
    private final ActivationCodeRepository activationCodeRepository;
    private final LoginAttemptPolicyRepository loginAttemptPolicyRepository;
    private final JwtProvider jwtProvider;
    private final AmqpPublisher amqpPublisher;

    @Override
    @Transactional
    public Map<String, String> registration(RegistrationRequest request, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult).processPassword(
                request.getPassword(), request.getPasswordConfirm());
        if (!userRepository.isUserExistByEmail(request.getEmail())) {
            User user = createUserAndSave(
                    request.getEmail(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getPassword());
            return Map.of("message", "User created successfully. You're username is " + user.getUsername()
                    + " Once logged in, you will be able to change your username.");
        }
        throw new ApiRequestException(EMAIL_ALREADY_TAKEN, HttpStatus.CONFLICT);
    }

    @Override
    @Transactional
    public Map<String, String> sendRegistrationCode(String email, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        User user = getUserByEmail(email, User.class);
        validateUserForRegistration(user);
        sendRegistrationEmail(user);
        return Map.of("message", "Activation Code was sent to your email.");
    }

    @Override
    @Transactional
    public Map<String, Object> checkRegistrationCode(String code) {
        ActivationCode ac = getActivationCodeByHashedCode(code);
        isActivationCodeExpired(ac.getExpirationTime());
        User user = checkIsUserVerified(ac.getUser());

        String randomCode = userServiceHelper.generateRandomCode();
        user = updateUsersDevice(user, randomCode);
        notifyUserCreation(user);
        String deviceToken = jwtProvider.createDeviceToken(randomCode);

        return Map.of(
                "deviceToken",
                deviceToken,
                "message",
                "You've activated your account and you're now ready to use it! Try and login to access your account.");
    }

    private User createUserAndSave(String email, String firstName, String lastName, String rawPassword) {
        String username = generateUniqueUsername(lastName, firstName);
        LoginAttemptPolicy policy = loginAttemptPolicyRepository.findById(1L).orElseThrow(
                () -> new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));
        User user = new User(
                email,
                firstName,
                lastName,
                username,
                userServiceHelper.encodePassword(rawPassword),
                policy);
        return userRepository.save(user);
    }

    private String generateUniqueUsername(String lastName, String firstName) {
        boolean usernameTaken = true;
        String username = "";
        while (usernameTaken) {
            username = lastName + "_" + firstName + userServiceHelper.generateRandomSuffix();
            usernameTaken = userRepository.existsUserByUsername(username);
        }
        return username;
    }

    private <T> T getUserByEmail(String email, Class<T> clazz) {
        return userRepository.getUserByEmail(email, clazz)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private void generateAndSaveActivationCode(User user, String randomCode) {
        String hashedRandomCode = userServiceHelper.hash(Base64.getUrlDecoder().decode(randomCode));
        ActivationCode code = new ActivationCode(hashedRandomCode, ActivationCodeType.ACTIVATION, user);
        activationCodeRepository.save(code);
    }

    private void sendRegistrationEmail(User user) {
        String randomCode = userServiceHelper.generateRandomCode();
        generateAndSaveActivationCode(user, randomCode);
        EmailRequest emailRequest = buildRegistrationEmail(user, randomCode);
        amqpPublisher.sendEmail(emailRequest);
    }

    private EmailRequest buildRegistrationEmail(User user, String randomCode) {
        return new EmailRequest.Builder(
                user.getEmail(), "Registration Code", "registration-template").attributes(
                        Map.of(
                                "fullName", user.getFirstName() + " " + user.getLastName(),
                                "registrationCode", randomCode))
                .build();
    }

    private User checkIsUserVerified(User user) {
        validateUserForRegistration(user);
        user.setVerified(true);
        return user;
    }

    private void validateUserForRegistration(User user) {
        if (user.isVerified()) {
            throw new ApiRequestException(ACCOUNT_ALREADY_VERIFIED, HttpStatus.BAD_REQUEST);
        }
    }

    private User updateUsersDevice(User user, String randomCode) {
        UserDevice userDevice = new UserDevice();
        userDevice.setDeviceName("register_device"); // default device name
        userDevice.setDeviceKey(userServiceHelper.hash(Base64.getUrlDecoder().decode(randomCode)));
        userDevice.setUser(user);
        user.getUserDevices().add(userDevice);
        return userRepository.save(user);
    }

    private void isActivationCodeExpired(LocalDateTime expirationTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(expirationTime))
            throw new ApiRequestException(ACTIVATION_CODE_EXPIRED, HttpStatus.GONE);
    }

    private void notifyUserCreation(User user) {
        UserPrincipalProjection updatedUser = userRepository
                .getUserByEmail(user.getEmail(), UserPrincipalProjection.class)
                .orElseThrow(() -> new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));
        // if error = rollback changes to database
        amqpPublisher.userCreated(updatedUser);
    }

    private ActivationCode getActivationCodeByHashedCode(String encodedCode) {
        String hashedCode = decodeAndHashRegistrationCode(encodedCode);
        return activationCodeRepository.getActivationCodeByHashedCode(hashedCode, ActivationCode.class)
                .orElseThrow(() -> new ApiRequestException(ACTIVATION_CODE_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private String decodeAndHashRegistrationCode(String encodedCode) {
        return userServiceHelper.hash(Base64.getUrlDecoder().decode(encodedCode));
    }
}
