package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.MagicCodeType;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.mapper.FieldErrorMapper.ValidationContext;
import dev.twiceb.common.records.AuthUserRecord;
import dev.twiceb.common.records.AuthenticatedUserRecord;
import dev.twiceb.common.security.JwtProvider;
import dev.twiceb.userservice.amqp.AmqpPublisher;
import dev.twiceb.userservice.dto.request.AuthContextRequest;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.MetadataDto;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.enums.ActivationCodeType;
import dev.twiceb.userservice.model.*;
import dev.twiceb.userservice.repository.*;
import dev.twiceb.userservice.repository.projection.UserPrincipalProjection;
import dev.twiceb.userservice.service.LoginAttemptService;
import dev.twiceb.userservice.service.RegistrationService;
import dev.twiceb.userservice.service.util.MagicCodeProvider;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import static dev.twiceb.common.constants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final UserServiceHelper userServiceHelper;
    private final ActivationCodeRepository activationCodeRepository;
    private final LoginAttemptPolicyRepository loginAttemptPolicyRepository;
    private final LoginAttemptService loginAttemptService;
    private final MagicCodeProvider magicCodeProvider;
    private final EmailAuthProvider emailAuthProvider;
    private final JwtProvider jwtProvider;
    private final AmqpPublisher amqpPublisher;

    @Override
    @Transactional
    public Map<String, String> registration(RegistrationRequest request,
            BindingResult bindingResult) {
        userServiceHelper.processInputErrors(bindingResult, ValidationContext.SIGN_UP);
        userServiceHelper.processPassword(request.getPassword(), request.getPasswordConfirm());

        if (!userRepository.isUserExistByEmail(request.getEmail())) {
            User user = createUserAndSave(request.getEmail(), request.getPassword(), true);
            return Map.of("message",
                    "User created successfully. You're username is " + user.getUsername()
                            + " Once logged in, you will be able to change your username.");
        }
        throw new AuthException(AuthErrorCodes.USER_ALREADY_EXIST);
    }

    @Override
    public AuthenticatedUserRecord magicRegistration(MagicCodeRequest request,
            BindingResult bindingResult) {
        userServiceHelper.processInputErrors(bindingResult, ValidationContext.MAGIC_SIGN_UP);
        // throw error if user already exist
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(AuthErrorCodes.USER_ALREADY_EXIST);
        }

        return validateMagicUser(request.getEmail(), request.getMagicCode());
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

        return Map.of("deviceToken", deviceToken, "message",
                "You've activated your user and you're now ready to use it! Try and login to access your user.");
    }

    private AuthenticatedUserRecord validateMagicUser(String email, String magicCode) {
        MagicCodeType type = MagicCodeType.MAGIC_LINK;
        String ipAddress = getIpAddress();
        email = magicCodeProvider.validateAndGetEmail(email, magicCode, type, ipAddress);
        // we won't even use the password for auth
        User user = createUserAndSave(email, UUID.randomUUID().toString(), false);
        user.setEmailVerified(true);
        user.setPasswordAutoSet(true);
        String randomCode = userServiceHelper.generateRandomCode();
        user = updateUsersDevice(user, randomCode);
        notifyUserCreation(user);
        String deviceToken = jwtProvider.createDeviceToken(randomCode);
        String jwt = jwtProvider.createToken(email, "USER");

        loginAttemptService.generateLoginAttempt(true, true, user, getIpAddress());

        AuthUserRecord authUser = new AuthUserRecord(user.getId(), user.getEmail(), null, null);
        return new AuthenticatedUserRecord(authUser, jwt, deviceToken);
    }

    private User createUserAndSave(String email, String password, boolean save) {
        LoginAttemptPolicy policy = loginAttemptPolicyRepository.findById(1L)
                .orElseThrow(() -> new ApiRequestException(INTERNAL_SERVER_ERROR,
                        HttpStatus.INTERNAL_SERVER_ERROR));
        User user = new User();
        user.setEmail(email);
        user.setPassword(userServiceHelper.encodePassword(password));
        user.setLoginAttemptPolicy(policy);
        if (save) {
            return userRepository.save(user);
        }
        return user;
    }

    private <T> T getUserByEmail(String email, Class<T> clazz) {
        return userRepository.getUserByEmail(email, clazz)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private void generateAndSaveActivationCode(User user, String randomCode) {
        String hashedRandomCode = userServiceHelper.hash(Base64.getUrlDecoder().decode(randomCode));
        ActivationCode code =
                new ActivationCode(hashedRandomCode, ActivationCodeType.ACTIVATION, user);
        activationCodeRepository.save(code);
    }

    private void sendRegistrationEmail(User user) {
        String randomCode = userServiceHelper.generateRandomCode();
        generateAndSaveActivationCode(user, randomCode);
        EmailRequest emailRequest = buildRegistrationEmail(user, randomCode);
        amqpPublisher.sendEmail(emailRequest);
    }

    private EmailRequest buildRegistrationEmail(User user, String randomCode) {
        return new EmailRequest.Builder(user.getEmail(), "Registration Code",
                "registration-template")
                        .attributes(
                                Map.of("fullName", user.getFirstName() + " " + user.getLastName(),
                                        "registrationCode", randomCode))
                        .build();
    }

    private User checkIsUserVerified(User user) {
        validateUserForRegistration(user);
        user.setEmailVerified(true);
        return user;
    }

    private void validateUserForRegistration(User user) {
        if (user.isEmailVerified()) {
            throw new ApiRequestException(ACCOUNT_ALREADY_VERIFIED, HttpStatus.CONFLICT);
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
        UserPrincipalProjection updatedUser =
                userRepository.getUserByEmail(user.getEmail(), UserPrincipalProjection.class)
                        .orElseThrow(() -> new ApiRequestException(INTERNAL_SERVER_ERROR,
                                HttpStatus.INTERNAL_SERVER_ERROR));
        // if error = rollback changes to database
        amqpPublisher.userCreated(updatedUser);
    }

    private ActivationCode getActivationCodeByHashedCode(String encodedCode) {
        String hashedCode = decodeAndHashRegistrationCode(encodedCode);
        return activationCodeRepository
                .getActivationCodeByHashedCode(hashedCode, ActivationCode.class)
                .orElseThrow(() -> new ApiRequestException(ACTIVATION_CODE_NOT_FOUND,
                        HttpStatus.NOT_FOUND));
    }

    private String decodeAndHashRegistrationCode(String encodedCode) {
        return userServiceHelper.hash(Base64.getUrlDecoder().decode(encodedCode));
    }

    private String getIpAddress() {
        HttpServletRequest request = getRequest();

        return request.getHeader("x-forwarded-for");
    }

    @SuppressWarnings("null")
    private HttpServletRequest getRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) attributes).getRequest();
    }

    @Override
    public AuthenticatedUserRecord signUp(AuthContextRequest<RegistrationRequest> request) {
        RegistrationRequest payload = request.getPayload();

        throw new UnsupportedOperationException("Unimplemented method 'signUp'");
    }
}
