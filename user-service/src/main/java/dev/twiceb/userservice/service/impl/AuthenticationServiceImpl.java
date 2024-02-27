package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.enums.UserRole;
import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.exception.NoRollbackApiRequestException;
import dev.twiceb.common.security.JwtProvider;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.PasswordResetRequest;
import dev.twiceb.userservice.model.*;
import dev.twiceb.userservice.repository.*;
import dev.twiceb.userservice.repository.projection.AuthUserProjection;
import dev.twiceb.userservice.repository.projection.UserPrincipalProjection;
import dev.twiceb.userservice.repository.projection.UserUsernameProjection;
import dev.twiceb.userservice.service.*;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserRepository userRepository;
    private final DeviceService deviceService;
    private final UserServiceHelper userServiceHelper;
    private final EmailService emailService;
    private final JwtProvider jwtProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getAuthenticatedUserId() {
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(noRollbackFor = {NoRollbackApiRequestException.class})
    public Map<String, Object> login(AuthenticationRequest request, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);

        LocalDateTime currentTime = LocalDateTime.now();
        Map<String, String> customHeaders = getDeviceKeyAndIp();
        User user = findUserByUsername(request.getUsername());
        validateUser(user, customHeaders, currentTime);
        checkDeviceKeyAndHandleStatus(user, customHeaders, currentTime);
        handleLoginAttempts(request.getPassword(), user, customHeaders);

        String token = jwtProvider.createToken(user.getEmail(), user.getRole().toString());
        return Map.of("user", user, "token", token);
    }

    @Override
    public Map<String, Object> getUserByToken() {
        AuthUserProjection user = userRepository.getUserById(getUserId(), AuthUserProjection.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        String token = jwtProvider.createToken(user.getEmail(), UserRole.USER.name());
        return Map.of("user", user, "token", token);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> forgotUsername(String email, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        UserUsernameProjection user = userRepository.getUserByEmail(email, UserUsernameProjection.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND_WITH_EMAIL, HttpStatus.NOT_FOUND));
        emailService.sendUsernameToUsersEmail(user.getUsername(), email);
        return Map.of("message", "Check your email for your username");
    }

    @Override
    @Transactional
    public Map<String, String> resetPassword(PasswordResetRequest request, String token, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult).processPassword(
                request.getPassword(), request.getConfirmPassword()
        );
        PasswordResetToken resetToken = passwordResetService.validateAndExpirePasswordResetToken(token);
        User user = resetToken.getUser();
        resetValidPassword(user, request.getPassword());
        return Map.of("message", "Password has been reset. Try to login with your new password");
    }

    @Override
    @Transactional(noRollbackFor = {NoRollbackApiRequestException.class})
    public Map<String, Object> newDeviceVerification(String deviceVerificationCode, boolean trust) {
        String hashedDeviceCode = decodeAndHashDeviceVerificationCode(deviceVerificationCode);
        User user = deviceService.retrieveAndValidateDeviceVerificationCode(hashedDeviceCode);

        if (!trust) {
            lockUserAndSendEmail(user);
            throw new NoRollbackApiRequestException(AUTHORIZATION_ERROR, HttpStatus.LOCKED);
        }

        loginAttemptService.updateLoginAttempt(user.getId());
        String deviceKey = userServiceHelper.generateRandomCode();
        user = userRepository.save(deviceService.verifyNewDevice(user, hashValue(Base64.getUrlDecoder().decode(deviceKey))));
        String token = jwtProvider.createToken(user.getEmail(), user.getRole().toString());
        String deviceToken = jwtProvider.createDeviceToken(deviceKey);

        return Map.of("user", user, "token", token, "deviceToken", deviceToken);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Map<String, String> forgotPassword(String email, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        User user = userRepository.getUserByEmail(email, User.class).orElseThrow(
                () -> new ApiRequestException(USER_NOT_FOUND_WITH_EMAIL, HttpStatus.NOT_FOUND)
        );
        String otp = userServiceHelper.generateOTP();
        String hashedOtp = hashValue(otp);
        return passwordResetService.createAndSendPasswordResetOtpEmail(user, otp, hashedOtp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Map<String, String> verifyOtp(String otp, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        String token = userServiceHelper.generateRandomCode();
        return passwordResetService.verifyOtp(hashValue(otp), token);
    }

    private void createAndSendPasswordResetOtpEmail(User user) {
        String otp = userServiceHelper.generateOTP();
        String hashedOtp = hashValue(otp);
        passwordResetService.createAndSendPasswordResetOtpEmail(user, otp, hashedOtp);
    }

    private void resetValidPassword(User user, String requestPassword) {
        if (userServiceHelper.isPasswordsEqual(requestPassword, user.getPassword())) {
            user.setPassword(userServiceHelper.encodePassword(requestPassword));
            userRepository.save(user);
        }
        throw new ApiRequestException(SAME_SAVED_PASSWORD, HttpStatus.BAD_REQUEST);
    }

    private String decodeAndHashDeviceVerificationCode(String deviceVerificationCode) {
        return userServiceHelper.hash(Base64.getUrlDecoder().decode(deviceVerificationCode));
    }

    private User findUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            throw new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
        return user;
    }

    private void validateUser(User user, Map<String, String> customHeaders, LocalDateTime currentTime) {
        if (!user.isVerified()) {
            throw new ApiRequestException(VERIFY_ACCOUNT_WITH_EMAIL, HttpStatus.BAD_REQUEST);
        }

        if (user.getUserStatus().equals(UserStatus.LOCKED)) {
            handleLockedUser(user, customHeaders, currentTime);
        }
    }

    private void checkDeviceKeyAndHandleStatus(User user, Map<String, String> customHeaders, LocalDateTime currentTime) {
        if (!deviceService.verifyDevice(user.getId(), customHeaders.get(AUTH_USER_DEVICE_KEY))) {
            handleUnverifiedDevice(user, customHeaders);
        }
    }

    private void handleUnverifiedDevice(User user, Map<String, String> customHeaders) {
        String randomCode = userServiceHelper.generateRandomCode();
        String hashedRandomCode = userServiceHelper.hash(Base64.getUrlDecoder().decode(randomCode));
        user.setUserStatus(UserStatus.PENDING_USER_CONFIRMATION);
        user = userRepository.save(user);

        loginAttemptService.generateLoginAttempt(false, true, user, customHeaders.get(AUTH_USER_IP_HEADER));
        deviceService.sendDeviceVerificationEmail(user, randomCode, hashedRandomCode, customHeaders);

        throw new NoRollbackApiRequestException(DEVICE_KEY_NOT_FOUND_OR_MATCH, HttpStatus.FORBIDDEN);
    }

    private void handleLockedUser(User user, Map<String, String> customHeaders, LocalDateTime currentTime) {
        loginAttemptService.handleLockedUser(user, customHeaders, currentTime);
    }

    private void handleLoginAttempts(String password, User user, Map<String, String> customHeaders) {
        boolean isPasswordMatch = userServiceHelper.isPasswordsEqual(password, user.getPassword());
        loginAttemptService.handleLoginAttempt(isPasswordMatch, user, customHeaders);
    }

    private void lockUserAndSendEmail(User user) {
        user = loginAttemptService.lockUser(user, "locked user per user request");
        createAndSendPasswordResetOtpEmail(user);
    }

    private String getDeviceKey() {
        HttpServletRequest request = getRequest();
        String deviceKey = request.getHeader(AUTH_USER_DEVICE_KEY);

        if (deviceKey == null || deviceKey.isEmpty()) {
            deviceKey = "";
        } else {
            // TODO: change this to Api-gateway?
            deviceKey = decodeAndHashDeviceVerificationCode(jwtProvider.parseDeviceToken(deviceKey));
        }
        return deviceKey;
    }

    private Map<String, String> getDeviceKeyAndIp() {
        HttpServletRequest request = getRequest();
        return Map.of(
                AUTH_USER_DEVICE_KEY, getDeviceKey(),
                AUTH_USER_IP_HEADER, request.getHeader(AUTH_USER_IP_HEADER),
                AUTH_USER_AGENT_HEADER, request.getHeader(AUTH_USER_AGENT_HEADER)
        );
    }

    private Long getUserId() {
        HttpServletRequest request = getRequest();
        return Long.parseLong(request.getHeader(AUTH_USER_ID_HEADER));
    }

    private HttpServletRequest getRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) attributes).getRequest();
    }

    private String hashValue(String value) {
        return userServiceHelper.hash(value);
    }

    private String hashValue(byte[] bytes) {
        return userServiceHelper.hash(bytes);
    }

}
