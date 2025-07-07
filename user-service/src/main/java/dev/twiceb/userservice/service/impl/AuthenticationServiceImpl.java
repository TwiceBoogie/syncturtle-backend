package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.exception.NoRollbackApiRequestException;
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

@Slf4j @Service @RequiredArgsConstructor
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
    public UUID getAuthenticatedUserId(){
        return getUserId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getAuthenticatedUser(){
        return userRepository.findById(getUserId())
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserPrincipalProjection getUserPrincipleByEmail(String email){
        return userRepository.getUserByEmail(email,UserPrincipalProjection.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public Map<String, Object> checkEmail(String email, BindingResult bindingResult){
        userServiceHelper.processBindingResults(bindingResult);
        boolean existing = false;
        String status = "MAGIC_CODE";
        boolean isPasswordAutoSet = true;
        // check if email-service is up (ping)
        Optional<User> optionalUser = userRepository.getUserByEmail(email,User.class);
        if (optionalUser.isPresent()) {
            existing = true;
            User user = optionalUser.get();
            if (!user.isPasswordAutoSet()) {
                status = "CREDENTIAL";
                isPasswordAutoSet = true;
            }
        }

        return Map.of("existing",existing,"status",status,"passwordAutoSet",isPasswordAutoSet);
    }

    @Override
    public String generateMagicCode(String email, BindingResult bindingResult){
        userServiceHelper.processBindingResults(bindingResult);
        Pair<String, String> keyAndToken = magicCodeProvider.initiate(email);
        emailService.sendMagicCodeEmail(email,keyAndToken.getSecond());
        return keyAndToken.getFirst();
    }

    @Override
    public Map<String, Object> magicLogin(MagicCodeRequest request, BindingResult bindingResult){
        userServiceHelper.processBindingResults(bindingResult);
        User user = findUserByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(AuthErrorCodes.USER_DOES_NOT_EXIST));

        magicCodeProvider.validateAndGetEmail("magic_" + request.getEmail(),request.getMagicCode());
        return validateAndAuthenticateUser(user,"");
    }

    /**
     * {@inheritDoc}
     */
    @Override @Transactional(noRollbackFor = { NoRollbackApiRequestException.class })
    public Map<String, Object> login(AuthenticationRequest request, BindingResult bindingResult){
        userServiceHelper.processBindingResults(bindingResult);

        return findUserByEmail(request.getEmail())
                .map(user -> validateAndAuthenticateUser(user,request.getPassword()))
                .orElseThrow(() -> new ApiRequestException("Invalid username or password",
                        HttpStatus.UNAUTHORIZED));
    }

    @Override @Transactional(readOnly = true)
    public Map<String, Object> getUserByToken(){
        AuthUserProjection user = userRepository.getUserById(getUserId(),AuthUserProjection.class)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND));
        // String token = jwtProvider.createToken(user.getEmail(),
        // UserRole.USER.name());
        return Map.of("user",user);
    }

    /**
     * {@inheritDoc}
     */
    @Override @Transactional(readOnly = true)
    public Map<String, String> forgotUsername(String email, BindingResult bindingResult){
        userServiceHelper.processBindingResults(bindingResult);

        return userRepository.getUserByEmail(email,UserUsernameProjection.class).map(user -> {
            sendUsernameEmail(user.getUsername(),email);
            return Map.of("message","Check your email for your username");
        }).orElseThrow(
                () -> new ApiRequestException(USER_NOT_FOUND_WITH_EMAIL, HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override @Transactional
    public Map<String, String> forgotPassword(String email, BindingResult bindingResult){
        userServiceHelper.processBindingResults(bindingResult);
        return userRepository.getUserByEmail(email,User.class)
                .map(passwordResetService::createAndSendPasswordResetOtpEmail)
                .orElseThrow(() -> new ApiRequestException(USER_NOT_FOUND_WITH_EMAIL,
                        HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> verifyOtp(String otp, BindingResult bindingResult){
        userServiceHelper.processBindingResults(bindingResult);
        return passwordResetService.verifyOtp(otp);
    }

    /**
     * {@inheritDoc}
     */
    @Override @Transactional
    public Map<String, String> resetPassword(PasswordResetRequest request, String token,
            BindingResult bindingResult){
        userServiceHelper.processBindingResults(bindingResult);
        userServiceHelper.processPassword(request.getPassword(),request.getConfirmPassword());
        return passwordResetService.validateAndExpirePasswordResetToken(token).map(resetToken -> {
            User user = resetToken.getUser();
            resetValidPassword(user,request.getPassword());
            return Map.of("message","Password has been reset. Try to login with your new password");
        }).orElseThrow(() -> new ApiRequestException("Password reset token is invalid.",
                HttpStatus.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     */
    @Override @Transactional(noRollbackFor = { NoRollbackApiRequestException.class })
    public Map<String, Object> newDeviceVerification(String deviceVerificationCode, boolean trust){
        return deviceService.retrieveAndValidateDeviceVerificationCode(deviceVerificationCode)
                .map(user -> handleDeviceVerification(user,trust)).orElseThrow(
                        () -> new ApiRequestException("User not found after device verification.",
                                HttpStatus.FORBIDDEN));
    }

    private Map<String, Object> handleDeviceVerification(User user, boolean trust){
        if (!trust) {
            lockUserAndSendEmail(user);
            throw new NoRollbackApiRequestException(AUTHORIZATION_ERROR, HttpStatus.LOCKED);
        }
        updateLoginAttempt(user.getId());
        return processNewDeviceVerification(user);
    }

    private Map<String, Object> processNewDeviceVerification(User user){
        String deviceKey = userServiceHelper.generateRandomCode();
        user = userRepository.save(deviceService.processNewDevice(user,deviceKey));
        String token = jwtProvider.createToken(user.getEmail(),user.getRole().toString());
        String deviceToken = jwtProvider.createDeviceToken(deviceKey);

        return Map.of("user",user,"token",token,"deviceToken",deviceToken);
    }

    private void updateLoginAttempt(UUID userId){
        loginAttemptService.updateLoginAttempt(userId);
    }

    private void sendUsernameEmail(String username, String email){
        emailService.sendUsernameToUsersEmail(username,email);
    }

    private void createAndSendPasswordResetOtpEmail(User user){
        passwordResetService.createAndSendPasswordResetOtpEmail(user);
    }

    private void resetValidPassword(User user, String requestPassword){
        if (userServiceHelper.isPasswordsEqual(requestPassword,user.getPassword())) {
            throw new ApiRequestException(SAME_SAVED_PASSWORD, HttpStatus.BAD_REQUEST);
        }
        user.setPassword(userServiceHelper.encodePassword(requestPassword));
        userRepository.save(user);
    }

    private Optional<User> findUserByEmail(String email){
        return userRepository.getUserByEmail(email,User.class);
    }

    private Map<String, Object> validateAndAuthenticateUser(User user, String password){
        LocalDateTime currentTime = LocalDateTime.now();
        Map<String, String> customHeaders = getDeviceKeyAndIp();
        validateUser(user,customHeaders,currentTime);
        checkDeviceKeyAndHandleStatus(user,customHeaders);
        if (password.isBlank()) {
            handleLoginAttempts(user,customHeaders);
        } else {
            handleLoginAttempts(password,user,customHeaders);
        }
        String token = jwtProvider.createToken(user.getEmail(),user.getRole().toString());
        return Map.of("user",user,"token",token);
    }

    // handles user's status and handle them accordingly
    private void validateUser(User user, Map<String, String> customHeaders,
            LocalDateTime currentTime){
        switch (user.getUserStatus()) {
        case ACTIVE -> handleActiveUser(user);
        case SUSPENDED -> handleSuspendedUser(user);
        case LOCKED -> handleLockedUser(user,customHeaders,currentTime);
        case PENDING_USER_CONFIRMATION -> handlePendingUser(user,customHeaders,currentTime);
        case null, default -> throw new ApiRequestException("Unknown user status",
                HttpStatus.BAD_REQUEST);
        }
    }

    private void handleActiveUser(User user){
        if (!user.isVerified()) {
            throw new ApiRequestException(VERIFY_ACCOUNT_WITH_EMAIL, HttpStatus.BAD_REQUEST);
        }
    }

    private void handleSuspendedUser(User user){

    }

    private void handlePendingUser(User user, Map<String, String> customHeaders,
            LocalDateTime currentTime){
        if (deviceService.isDeviceVerificationCodeSent(user.getId(),currentTime)) {
            // TODO: should I create a login attempt on these?
            throw new NoRollbackApiRequestException(DEVICE_KEY_NOT_FOUND_OR_MATCH,
                    HttpStatus.FORBIDDEN);
        } else {
            handleUnverifiedDevice(user,customHeaders);
        }
    }

    private void checkDeviceKeyAndHandleStatus(User user, Map<String, String> customHeaders){
        if (!deviceService.verifyDevice(user.getId(),customHeaders.get(AUTH_USER_DEVICE_KEY))) {
            handleUnverifiedDevice(user,customHeaders);
        }
    }

    private void handleUnverifiedDevice(User user, Map<String, String> customHeaders){
        user.setUserStatus(UserStatus.PENDING_USER_CONFIRMATION);
        user = userRepository.save(user);
        loginAttemptService.generateLoginAttempt(false,true,user,
                customHeaders.get(AUTH_USER_IP_HEADER));
        deviceService.sendDeviceVerificationEmail(user,customHeaders);

        throw new NoRollbackApiRequestException(DEVICE_KEY_NOT_FOUND_OR_MATCH,
                HttpStatus.FORBIDDEN);
    }

    private void handleLockedUser(User user, Map<String, String> customHeaders,
            LocalDateTime currentTime){
        loginAttemptService.handleLockedUser(user,customHeaders,currentTime);
    }

    private void handleLoginAttempts(String password, User user, Map<String, String> customHeaders){
        boolean isPasswordMatch = userServiceHelper.isPasswordsEqual(password,user.getPassword());
        loginAttemptService.handleLoginAttempt(isPasswordMatch,user,customHeaders);
    }

    private void handleLoginAttempts(User user, Map<String, String> customHeaders){
        loginAttemptService.handleLoginAttempt(user,customHeaders);
    }

    private void lockUserAndSendEmail(User user){
        user = loginAttemptService.lockUser(user,"locked user per user request");
        createAndSendPasswordResetOtpEmail(user);
    }

    private String getDeviceKey(HttpServletRequest request){
        return Objects.toString(request.getHeader(AUTH_USER_DEVICE_KEY),"");
    }

    private Map<String, String> getDeviceKeyAndIp(){
        HttpServletRequest request = getRequest();

        return Map.of(AUTH_USER_DEVICE_KEY,getDeviceKey(request),AUTH_USER_IP_HEADER,
                request.getHeader("X-Forwarded-For"),AUTH_USER_AGENT_HEADER,
                request.getHeader("user-agent"));
    }

    private UUID getUserId(){
        HttpServletRequest request = getRequest();
        return UUID.fromString(request.getHeader(AUTH_USER_ID_HEADER));
    }

    @SuppressWarnings("null")
    private HttpServletRequest getRequest(){
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) attributes).getRequest();
    }

    // private Map<String, String> extractHeaders(HttpServletRequest request) {
    // Map<String, String> headersMap = new HashMap<>();
    // Enumeration<String> headerNames = request.getHeaderNames();
    // while (headerNames.hasMoreElements()) {
    // String headerName = headerNames.nextElement();
    // headersMap.put(headerName, request.getHeader(headerName));
    // }
    // return headersMap;
    // }

}
