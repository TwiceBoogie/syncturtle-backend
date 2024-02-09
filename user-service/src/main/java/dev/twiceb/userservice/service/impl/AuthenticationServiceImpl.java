package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.common.dto.request.NotificationRequest;
import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.security.JwtProvider;
import dev.twiceb.userservice.amqp.AmqpPublisher;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.enums.ActivationCodeType;
import dev.twiceb.userservice.feign.NotificationClient;
import dev.twiceb.userservice.model.*;
import dev.twiceb.userservice.repository.ActivationCodeRepository;
import dev.twiceb.userservice.repository.LoginAttemptPolicyRepository;
import dev.twiceb.userservice.repository.LoginAttemptRepository;
import dev.twiceb.userservice.repository.UserRepository;
import dev.twiceb.userservice.repository.projection.AuthUserProjection;
import dev.twiceb.userservice.repository.projection.UserPrincipalProjection;
import dev.twiceb.userservice.service.AuthenticationService;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ua_parser.Client;
import ua_parser.Parser;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static dev.twiceb.common.constants.ErrorMessage.*;
import static dev.twiceb.common.constants.PathConstants.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final LoginAttemptRepository loginAttemptRepository;
    private final ActivationCodeRepository activationCodeRepository;
    private final UserServiceHelper userServiceHelper;
    private final AmqpPublisher amqpPublisher;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Override
    public Long getAuthenticatedUserId() {
        return getUserId();
    }

    @Override
    public User getAuthenticatedUser() {
        return null;
    }

    @Override
    public UserPrincipalProjection getUserPrincipleByEmail(String email) {
        return userRepository.getUserByEmail(email, UserPrincipalProjection.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional
    public Map<String, Object> login(AuthenticationRequest request, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);
        LocalDateTime currentTime = LocalDateTime.now();
        Map<String, String> customHeaders = getDeviceKeyAndIp();
        User user = userRepository.getUserByEmail(request.getEmail(), User.class)
                .orElseThrow(
                        () -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.BAD_REQUEST));

        if (!isUserDeviceExist(customHeaders.get(AUTH_USER_DEVICE_KEY), user.getUserDevices())) {
            user.setUserStatus(UserStatus.PENDING_USER_CONFIRMATION);
            user = userRepository.save(user);
            generateLoginAttempt(false, true, user, customHeaders.get(AUTH_USER_IP_HEADER));
            sendDeviceVerificationEmail(
                    user, customHeaders.get(AUTH_USER_IP_HEADER), customHeaders.get(AUTH_USER_AGENT_HEADER)
            );
            throw new ApiRequestException(DEVICE_KEY_NOT_FOUND_OR_MATCH, HttpStatus.FORBIDDEN);
        }

        if (user.getUserStatus().equals(UserStatus.LOCKED)) {
            LockedUser userLock = user.getLockedUser().getLast();
            Duration duration = Duration.between(currentTime, userLock.getLockoutEnd());

            if (currentTime.isBefore(userLock.getLockoutEnd())) {
                generateLoginAttempt(false, false, user, customHeaders.get(AUTH_USER_IP_HEADER));
                throw new ApiRequestException(LOCKED_ACCOUNT_AFTER_N_ATTEMPTS + duration, HttpStatus.TOO_MANY_REQUESTS);
            }
            user.setUserStatus(UserStatus.ACTIVE);
        }

        if (!user.isVerified()) {
            throw new ApiRequestException(VERIFY_ACCOUNT_WITH_EMAIL, HttpStatus.BAD_REQUEST);
        }

        LoginAttemptPolicy policy = user.getLoginAttemptPolicy();
        LocalDateTime startDate = calculateResetStartDate(policy.getResetDuration());
        // if there is a success attempt (true), then it won't count failed attempts
        boolean countUserAttempts = checkLoginAttempts(user.getId(), startDate);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            if (!countUserAttempts) {
                int failedAttempts = countFailedLoginAttempts(user.getId(), startDate) + 1;
                if (failedAttempts >= policy.getMaxAttempts()) {
                    user = lockUser(user);
                }
                generateLoginAttempt(false, false, user, customHeaders.get(AUTH_USER_IP_HEADER));
            }
            throw new ApiRequestException(INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST);
        }

        generateLoginAttempt(true, false, user, customHeaders.get(AUTH_USER_IP_HEADER));
        String token = jwtProvider.createToken(user.getEmail(), user.getRole().toString());

        return Map.of("user", user, "token", token);
    }

    @Override
    public Map<String, String> forgotPassword(String email, BindingResult bindingResult) {
        userServiceHelper.processBindingResults(bindingResult);

        return null;
    }

    @Override
    @Transactional
    public Map<String, Object> newDeviceVerification(String deviceVerificationCode, boolean trust) {
        LocalDateTime currentTime = LocalDateTime.now();
        ActivationCode ac = activationCodeRepository.getActivationCodeByHashedCode(
                deviceVerificationCode, ActivationCode.class
        ).orElseThrow(() -> new ApiRequestException("no ac found", HttpStatus.FORBIDDEN));

        User user = ac.getUser();
        if (currentTime.isAfter(ac.getExpirationTime())) {
            throw new ApiRequestException(DEVICE_VERIFICATION_EXPIRED, HttpStatus.BAD_REQUEST);
        }
        if (!trust) {
            lockUser(user);
            // TODO: I should create a password reset token and send it to the user.
            throw new ApiRequestException(AUTHORIZATION_ERROR, HttpStatus.FORBIDDEN);
        }
        String deviceKey = userServiceHelper.regenerateActivationCode();
        user = addAndUpdateUserDevices(user, deviceKey);
        LoginAttempt loginAttempt = loginAttemptRepository.findRecentLoginAttempt(user.getId());
        loginAttempt.setSuccess(true);
        loginAttemptRepository.save(loginAttempt);
        String token = jwtProvider.createToken(user.getEmail(), user.getRole().toString());
        String deviceToken = jwtProvider.createDeviceToken(userServiceHelper.encryptDeviceKey(deviceKey));

        return Map.of("user", user, "token", token, "deviceToken", deviceToken);
    }

    private User addAndUpdateUserDevices(User user, String deviceKey) {
        user.setUserStatus(UserStatus.ACTIVE);
        UserDevice newDevice = new UserDevice();
        newDevice.setUser(user);
        newDevice.setDeviceKey(deviceKey);
        user.getUserDevices().add(newDevice);
        return userRepository.save(user);
    }

    private User lockUser(User user) {
        LockedUser lockUser = new LockedUser();
        lockUser.setUser(user);
        lockUser.setLockoutReason("Too many failed attempts");
        user.getLockedUser().add(lockUser);
        user.setUserStatus(UserStatus.LOCKED);
        return userRepository.save(user);
    }

    private void sendDeviceVerificationEmail(User user, String ipAddress, String userAgent) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d 'at' hh:mma", Locale.ENGLISH);
        String formattedDateTime = currentTime.format(formatter);
        List<ActivationCode> codeList = new ArrayList<>();
        ActivationCode code = userServiceHelper.createActivationCodeEntity(user, ActivationCodeType.DEVICE_VERIFICATION);
        codeList.add(code);
        ActivationCode passwordReset = userServiceHelper.createActivationCodeEntity(user, ActivationCodeType.PASSWORD_RESET);
        codeList.add(passwordReset);
        activationCodeRepository.saveAll(codeList);

        EmailRequest emailRequest = new EmailRequest.Builder(
                user.getEmail(), "Device Verification", "deviceVerification-template").attributes(
                        Map.of(
                                "fullName", user.getFirstName() + " " + user.getLastName(),
                                "deviceVerificationCode", code.getHashedCode(),
                                "passwordResetCode", passwordReset.getHashedCode(),
                                "userIp", ipAddress,
                                "userAgent", userAgent,
                                "accessDate", formattedDateTime
                        ))
                .build();
        amqpPublisher.sendEmail(emailRequest);
    }

    private void generateLoginAttempt(boolean success, boolean newDevice, User user, String ipAddress) {
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setUser(user);
        loginAttempt.setSuccess(success);
        loginAttempt.setIpAddress(ipAddress);
        loginAttempt.setNewDevice(newDevice);
        loginAttemptRepository.save(loginAttempt);
    }

    private boolean isUserDeviceExist(String deviceToken, List<UserDevice> userDevices) {
        if (deviceToken == null || deviceToken.isEmpty()) return false;

        String decryptedDeviceKey = userServiceHelper.decryptDeviceKey(deviceToken);
        for (UserDevice userDevice : userDevices) {
            if (decryptedDeviceKey.equals(userDevice.getDeviceKey())) {
                return true;
            }
        }
        return false;
    }

    private Map<String, String> getDeviceKeyAndIp() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        String deviceKey = request.getHeader(AUTH_USER_DEVICE_KEY);
        if (deviceKey.isEmpty()) {
            deviceKey = "";
        }
        return Map.of(
                AUTH_USER_DEVICE_KEY, deviceKey,
                AUTH_USER_IP_HEADER, request.getHeader(AUTH_USER_IP_HEADER),
                AUTH_USER_AGENT_HEADER, request.getHeader(AUTH_USER_AGENT_HEADER)
        );
    }

    private Long getUserId() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        return Long.parseLong(request.getHeader(AUTH_USER_ID_HEADER));
    }

    private LocalDateTime calculateResetStartDate(Duration resetDuration) {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.minus(resetDuration);
    }

    private boolean checkLoginAttempts(Long userId, LocalDateTime startDate) {
        return loginAttemptRepository.isAttemptInResetDuration(userId, startDate);
    }

    private int countFailedLoginAttempts(Long userId, LocalDateTime startDate) {
        return loginAttemptRepository.countFailedAttempts(userId, startDate);
    }

    public String generateOTP(int length) {
        String numbers = "0123456789";
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        for (int i=0; i < length; i++) {
            int index = random.nextInt(numbers.length());
            otp.append(numbers.charAt(index));
        }

        return otp.toString();
    }
}
