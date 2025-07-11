package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.MagicCodeType;
import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.exception.NoRollbackApiRequestException;
import dev.twiceb.common.records.AuthUserRecord;
import dev.twiceb.common.records.AuthenticatedUserRecord;
import dev.twiceb.common.records.DeviceRequestMetadata;
import dev.twiceb.common.records.MagicCodeRecord;
import dev.twiceb.common.security.JwtProvider;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.PasswordResetRequest;
import dev.twiceb.userservice.model.*;
import dev.twiceb.userservice.repository.*;
import dev.twiceb.userservice.repository.projection.AuthUserProjection;
import dev.twiceb.userservice.repository.projection.UserPrincipalProjection;
import dev.twiceb.userservice.repository.projection.UserUsernameProjection;
import dev.twiceb.userservice.service.*;
import dev.twiceb.userservice.service.util.MagicCodeProvider;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.*;

import static dev.twiceb.common.constants.ErrorMessage.*;
import static dev.twiceb.common.constants.PathConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final LoginAttemptService loginAttemptService;
    private final PasswordResetService passwordResetService;
    private final DeviceService deviceService;
    private final UserRepository userRepository;
    private final UserServiceHelper userServiceHelper;
    private final EmailService emailService;
    private final MagicCodeProvider magicCodeProvider;
    private final JwtProvider jwtProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getAuthenticatedUserId() {
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getAuthenticatedUser() {
        return userRepository.findById(getUserId())
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserPrincipalProjection getUserPrincipleByEmail(String email) {
        return userRepository.getUserByEmail(email, UserPrincipalProjection.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public MagicCodeRecord checkEmail(String email, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        boolean existing = false;
        String status = "MAGIC_CODE";
        boolean isPasswordAutoSet = true;
        // check if email-service is up (ping)
        Optional<User> optionalUser = userRepository.getUserByEmail(email, User.class);
        if (optionalUser.isPresent()) {
            existing = true;
            User user = optionalUser.get();
            if (user.getUserStatus().equals(UserStatus.PENDING_USER_CONFIRMATION)) {
                throw new AuthException(AuthErrorCodes.DEVICE_NOT_RECOGNIZE);
            }
            if (!user.isPasswordAutoSet()) {
                status = "CREDENTIAL";
                isPasswordAutoSet = true;
            }
        }

        return new MagicCodeRecord(existing, status, isPasswordAutoSet);
    }

    @Override
    public String generateMagicCode(String email, MagicCodeType type, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        Pair<String, String> keyAndToken = magicCodeProvider.initiate(email, type);
        emailService.sendMagicCodeEmail(email, keyAndToken.getSecond());
        return keyAndToken.getFirst();
    }

    @Override
    public AuthenticatedUserRecord magicLogin(MagicCodeRequest request,
            BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        User user = findUserByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(AuthErrorCodes.USER_DOES_NOT_EXIST));

        magicCodeProvider.validateAndGetEmail(request.getEmail(), request.getMagicCode(),
                MagicCodeType.MAGIC_LINK);
        return validateAndAuthenticateUser(user, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(noRollbackFor = {NoRollbackApiRequestException.class})
    public AuthenticatedUserRecord login(AuthenticationRequest request,
            BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);

        return findUserByEmail(request.getEmail())
                .map(user -> validateAndAuthenticateUser(user, request.getPassword()))
                .orElseThrow(() -> new ApiRequestException("Invalid username or password",
                        HttpStatus.UNAUTHORIZED));
    }

    @Override
    @Transactional(readOnly = true)
    public AuthenticatedUserRecord getUserByToken() {
        AuthUserProjection user = userRepository.getUserById(getUserId(), AuthUserProjection.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        // String token = jwtProvider.createToken(user.getEmail(),
        // UserRole.USER.name());
        AuthUserRecord userRecord = new AuthUserRecord(user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
        return new AuthenticatedUserRecord(userRecord, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, String> forgotUsername(String email, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);

        return userRepository.getUserByEmail(email, UserUsernameProjection.class).map(user -> {
            sendUsernameEmail(user.getUsername(), email);
            return Map.of("message", "Check your email for your username");
        }).orElseThrow(
                () -> new ApiRequestException(USER_NOT_FOUND_WITH_EMAIL, HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Map<String, String> forgotPassword(String email, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        return userRepository.getUserByEmail(email, User.class)
                .map(passwordResetService::createAndSendPasswordResetOtpEmail)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND_WITH_EMAIL,
                        HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> verifyOtp(String otp, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        return passwordResetService.verifyOtp(otp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Map<String, String> resetPassword(PasswordResetRequest request, String token,
            BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        userServiceHelper.processPassword(request.getPassword(), request.getConfirmPassword());
        return passwordResetService.validateAndExpirePasswordResetToken(token).map(resetToken -> {
            User user = resetToken.getUser();
            resetValidPassword(user, request.getPassword());
            return Map.of("message",
                    "Password has been reset. Try to login with your new password");
        }).orElseThrow(() -> new ApiRequestException("Password reset token is invalid.",
                HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(noRollbackFor = {NoRollbackApiRequestException.class})
    public AuthenticatedUserRecord newDeviceVerification(String deviceVerificationCode,
            boolean trust) {
        return deviceService.retrieveAndValidateDeviceVerificationCode(deviceVerificationCode)
                .map(user -> handleDeviceVerification(user, trust)).orElseThrow(
                        () -> new ApiRequestException("User not found after device verification.",
                                HttpStatus.FORBIDDEN));
    }

    private AuthenticatedUserRecord handleDeviceVerification(User user, boolean trust) {
        if (!trust) {
            lockUserAndSendEmail(user);
            throw new NoRollbackApiRequestException(AUTHORIZATION_ERROR, HttpStatus.LOCKED);
        }
        updateLoginAttempt(user.getId());
        return processNewDeviceVerification(user);
    }

    private AuthenticatedUserRecord processNewDeviceVerification(User user) {
        String deviceKey = userServiceHelper.generateRandomCode();
        user = userRepository.save(deviceService.processNewDevice(user, deviceKey));
        String token = jwtProvider.createToken(user.getEmail(), user.getRole().toString());
        String deviceToken = jwtProvider.createDeviceToken(deviceKey);
        AuthUserRecord userRecord = new AuthUserRecord(user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
        return new AuthenticatedUserRecord(userRecord, token, deviceToken);
    }

    private void updateLoginAttempt(UUID userId) {
        loginAttemptService.updateLoginAttempt(userId);
    }

    private void sendUsernameEmail(String username, String email) {
        emailService.sendUsernameToUsersEmail(username, email);
    }

    private void createAndSendPasswordResetOtpEmail(User user) {
        passwordResetService.createAndSendPasswordResetOtpEmail(user);
    }

    private void resetValidPassword(User user, String requestPassword) {
        if (userServiceHelper.isPasswordsEqual(requestPassword, user.getPassword())) {
            throw new ApiRequestException(SAME_SAVED_PASSWORD, HttpStatus.BAD_REQUEST);
        }
        user.setPassword(userServiceHelper.encodePassword(requestPassword));
        userRepository.save(user);
    }

    private Optional<User> findUserByEmail(String email) {
        return userRepository.getUserByEmail(email, User.class);
    }

    private AuthenticatedUserRecord validateAndAuthenticateUser(User user, String password) {
        LocalDateTime currentTime = LocalDateTime.now();
        DeviceRequestMetadata metadata = getDeviceKeyAndIp();
        validateUser(user, metadata, currentTime);
        checkDeviceKeyAndHandleStatus(user, metadata);
        if (password.isBlank()) {
            handleLoginAttempts(user, metadata);
        } else {
            handleLoginAttempts(password, user, metadata);
        }
        String token = jwtProvider.createToken(user.getEmail(), user.getRole().toString());
        AuthUserRecord userRecord = new AuthUserRecord(user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName());
        return new AuthenticatedUserRecord(userRecord, token, null);
    }

    // handles user's status and handle them accordingly
    private void validateUser(User user, DeviceRequestMetadata metadata,
            LocalDateTime currentTime) {
        switch (user.getUserStatus()) {
            case ACTIVE -> handleActiveUser(user);
            case SUSPENDED -> handleSuspendedUser(user);
            case LOCKED -> handleLockedUser(user, metadata, currentTime);
            case PENDING_USER_CONFIRMATION -> handlePendingUser(user, metadata, currentTime);
            case null, default -> throw new ApiRequestException("Unknown user status",
                    HttpStatus.BAD_REQUEST);
        }
    }

    private void handleActiveUser(User user) {
        if (!user.isVerified()) {
            throw new ApiRequestException(VERIFY_ACCOUNT_WITH_EMAIL, HttpStatus.BAD_REQUEST);
        }
    }

    private void handleSuspendedUser(User user) {

    }

    private void handlePendingUser(User user, DeviceRequestMetadata metadata,
            LocalDateTime currentTime) {
        // if (deviceService.isDeviceVerificationCodeSent(user.getId(), currentTime)) {
        // // TODO: should I create a login attempt on these?
        // throw new NoRollbackApiRequestException(DEVICE_KEY_NOT_FOUND_OR_MATCH,
        // HttpStatus.FORBIDDEN);
        // } else {
        // handleUnverifiedDevice(user, metadata);
        // }
        loginAttemptService.generateLoginAttempt(false, true, user, metadata.ipAddress());
        throw new AuthException(AuthErrorCodes.DEVICE_NOT_RECOGNIZE);
    }

    private void checkDeviceKeyAndHandleStatus(User user, DeviceRequestMetadata metadata) {
        if (!deviceService.verifyDevice(user.getId(), metadata.deviceKey())) {
            handleUnverifiedDevice(user, metadata);
        }
    }

    private void handleUnverifiedDevice(User user, DeviceRequestMetadata metadata) {
        user.setUserStatus(UserStatus.PENDING_USER_CONFIRMATION);
        user = userRepository.save(user);
        loginAttemptService.generateLoginAttempt(false, true, user, metadata.ipAddress());
        Pair<String, String> keyAndToken =
                magicCodeProvider.initiate(user.getEmail(), MagicCodeType.DEVICE_VERIFICATION);
        emailService.sendDeviceVerificationEmail(user, keyAndToken.getSecond(),
                metadata.ipAddress(), metadata.userAgent());
        // deviceService.sendDeviceVerificationEmail(user, metadata);

        throw new AuthException(AuthErrorCodes.DEVICE_NOT_RECOGNIZE);
    }

    private void handleLockedUser(User user, DeviceRequestMetadata metadata,
            LocalDateTime currentTime) {
        loginAttemptService.handleLockedUser(user, metadata, currentTime);
    }

    private void handleLoginAttempts(String password, User user, DeviceRequestMetadata metadata) {
        boolean isPasswordMatch = userServiceHelper.isPasswordsEqual(password, user.getPassword());
        loginAttemptService.handleLoginAttempt(isPasswordMatch, user, metadata);
    }

    private void handleLoginAttempts(User user, DeviceRequestMetadata metadata) {
        loginAttemptService.handleLoginAttempt(user, metadata);
    }

    private void lockUserAndSendEmail(User user) {
        user = loginAttemptService.lockUser(user, "locked user per user request");
        createAndSendPasswordResetOtpEmail(user);
    }

    private String getDeviceKey(HttpServletRequest request) {
        return Objects.toString(request.getHeader(AUTH_USER_DEVICE_KEY), "");
    }

    private DeviceRequestMetadata getDeviceKeyAndIp() {
        HttpServletRequest request = getRequest();
        String deviceKey = getDeviceKey(request);
        String ipAddress = request.getHeader("X-Forwarded-For");
        String userAgent = request.getHeader("user-agent");

        return new DeviceRequestMetadata(deviceKey, ipAddress, userAgent);
    }

    private UUID getUserId() {
        HttpServletRequest request = getRequest();
        return UUID.fromString(request.getHeader(AUTH_USER_ID_HEADER));
    }

    @SuppressWarnings("null")
    private HttpServletRequest getRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) attributes).getRequest();
    }
}
