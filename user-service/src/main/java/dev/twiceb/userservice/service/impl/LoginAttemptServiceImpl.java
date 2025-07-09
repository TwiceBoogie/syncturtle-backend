package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.common.exception.NoRollbackApiRequestException;
import dev.twiceb.common.records.DeviceRequestMetadata;
import dev.twiceb.userservice.model.LockedUser;
import dev.twiceb.userservice.model.LoginAttempt;
import dev.twiceb.userservice.model.LoginAttemptPolicy;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.LoginAttemptRepository;
import dev.twiceb.userservice.repository.UserRepository;
import dev.twiceb.userservice.repository.projection.LoginAttemptProjection;
import dev.twiceb.userservice.service.LoginAttemptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static dev.twiceb.common.constants.ErrorMessage.INCORRECT_PASSWORD;
import static dev.twiceb.common.constants.ErrorMessage.LOCKED_ACCOUNT_AFTER_N_ATTEMPTS;

@Service
@RequiredArgsConstructor
public class LoginAttemptServiceImpl implements LoginAttemptService {

    private final LoginAttemptRepository loginAttemptRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void generateLoginAttempt(boolean success, boolean newDevice, User user,
            String ipAddress) {
        generateLoginAttemptInternal(success, newDevice, user, ipAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(noRollbackFor = NoRollbackApiRequestException.class)
    public void handleLoginAttempt(boolean isPasswordMatch, User user,
            DeviceRequestMetadata metadata) {
        LoginAttemptPolicy policy = user.getLoginAttemptPolicy();
        // The start date from which login attempts should be considered for
        // verification.
        LocalDateTime startDate = calculateResetStartDate(policy.getResetDuration());
        // if there is a success attempt (true), then it won't count failed attempts
        boolean countUserAttempts = checkLoginAttempts(user.getId(), startDate);

        if (!isPasswordMatch) {
            if (!countUserAttempts) {
                int failedAttempts = countFailedLoginAttempts(user.getId(), startDate) + 1;
                if (failedAttempts >= user.getLoginAttemptPolicy().getMaxAttempts()) {
                    user = lockUserInternal(user, "Too many failed login attempts");
                }
                generateLoginAttemptInternal(false, false, user, metadata.ipAddress());
            }
            throw new NoRollbackApiRequestException(INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST);
        }

        generateLoginAttemptInternal(true, false, user, metadata.ipAddress());
    }

    @Override
    public void handleLoginAttempt(User user, DeviceRequestMetadata metadata) {
        LoginAttemptPolicy policy = user.getLoginAttemptPolicy();
        // The start date from which login attempts should be considered for
        // verification.
        LocalDateTime startDate = calculateResetStartDate(policy.getResetDuration());
        // if there is a success attempt (true), then it won't count failed attempts
        boolean countUserAttempts = checkLoginAttempts(user.getId(), startDate);

        if (!countUserAttempts) {
            int failedAttempts = countFailedLoginAttempts(user.getId(), startDate) + 1;
            if (failedAttempts >= user.getLoginAttemptPolicy().getMaxAttempts()) {
                user = lockUserInternal(user, "Too many failed login attempts");
            }
            generateLoginAttemptInternal(false, false, user, metadata.ipAddress());
            return;
        }
        generateLoginAttemptInternal(true, false, user, metadata.ipAddress());
    }

    @Override
    @Transactional
    public void updateLoginAttempt(UUID userId) {
        LoginAttempt loginAttempt =
                loginAttemptRepository.findFirstByUserIdOrderByAttemptTimestampDesc(userId);
        // TODO: if login attempt is null then throw error
        loginAttempt.setSuccess(true);
        loginAttemptRepository.save(loginAttempt);
    }

    @Override
    @Transactional
    public User lockUser(User user, String reason) {
        return lockUserInternal(user, reason);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(noRollbackFor = NoRollbackApiRequestException.class)
    public void handleLockedUser(User user, DeviceRequestMetadata metadata,
            LocalDateTime currentTime) {
        LockedUser userLock = user.getLockoutHistory().getLast();
        Duration duration = Duration.between(currentTime, userLock.getLockoutEnd());

        if (currentTime.isBefore(userLock.getLockoutEnd())) {
            generateLoginAttemptInternal(false, false, user, metadata.ipAddress());
            throw new NoRollbackApiRequestException(LOCKED_ACCOUNT_AFTER_N_ATTEMPTS + duration,
                    HttpStatus.TOO_MANY_REQUESTS);
        }
        user.setUserStatus(UserStatus.ACTIVE);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public LoginAttemptProjection getRecentLoginAttempt(UUID userId) {
        return loginAttemptRepository.findRecentLoginAttempt(userId).orElse(null);
    }

    private User lockUserInternal(User user, String reason) {
        LockedUser lockUser = new LockedUser();
        lockUser.setUser(user);
        lockUser.setLockoutReason(reason);
        user.getLockoutHistory().add(lockUser);
        user.setUserStatus(UserStatus.LOCKED);
        return userRepository.save(user);
    }

    private void generateLoginAttemptInternal(boolean success, boolean newDevice, User user,
            String ipAddress) {
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setUser(user);
        loginAttempt.setSuccess(success);
        loginAttempt.setIpAddress(ipAddress);
        loginAttempt.setNewDevice(newDevice); // meaning that a new device is trying to log in
        loginAttemptRepository.save(loginAttempt);
    }

    private LocalDateTime calculateResetStartDate(Duration resetDuration) {
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.minus(resetDuration);
    }

    private boolean checkLoginAttempts(UUID userId, LocalDateTime startDate) {
        return loginAttemptRepository.isAttemptInResetDuration(userId, startDate);
    }

    private int countFailedLoginAttempts(UUID userId, LocalDateTime startDate) {
        return loginAttemptRepository.countFailedAttempts(userId, startDate);
    }
}
