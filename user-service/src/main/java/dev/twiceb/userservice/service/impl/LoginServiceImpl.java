package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.request.RequestMetadata;
import dev.twiceb.common.enums.AuthMedium;
import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.userservice.domain.enums.LoginContext;
import dev.twiceb.userservice.domain.model.LockedUser;
import dev.twiceb.userservice.domain.model.Login;
import dev.twiceb.userservice.domain.model.LoginPolicy;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.LockedUserRepository;
import dev.twiceb.userservice.domain.repository.LoginPolicyRepository;
import dev.twiceb.userservice.domain.repository.LoginRepository;
import dev.twiceb.userservice.domain.repository.UserRepository;
import dev.twiceb.userservice.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import static dev.twiceb.common.constants.ErrorMessage.LOCKED_ACCOUNT_AFTER_N_ATTEMPTS;
import static dev.twiceb.common.constants.ErrorMessage.REASON_INCORRECT_CREDENTIALS;
import static dev.twiceb.common.constants.ErrorMessage.REASON_TOO_MANY_ATTEMPTS;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    // static fields
    private static final int ESCALATE_EVERY_N = 3;
    private static final Duration BASE_EXTENSION = Duration.ofMinutes(5);
    private static final Duration MAX_LOCK_CAP = Duration.ofHours(2);

    private final LoginRepository loginRepository;
    private final LoginPolicyRepository lPolicyRepository;
    private final LockedUserRepository lEventRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void failure(User user, boolean newDevice, AuthMedium authMethod, LoginContext context,
            RequestMetadata meta) {
        // 0: setup
        LoginPolicy loginPolicy = resolveLoginPolicy(user);
        Duration resetDuration = loginPolicy.getResetDuration();
        Duration lockoutDuration = loginPolicy.getLockoutDuration();
        int maxAttempts = loginPolicy.getMaxAttempts(); // ex. 5m (nullable)

        if (user != null && isActiveLock(user)) {
            handleEscalationForLockedUser(user);
            Login attempt = Login.failure(user, REASON_TOO_MANY_ATTEMPTS, meta, authMethod, context,
                    newDevice, null);
            saveAttemptRequiresNew(attempt);
            // throw exception
        }

        // 1: window start; success/failures are counted in n-hr intervals
        Instant windowStart = Instant.now().minus(resetDuration);
        // flags
        boolean overLimit = false;

        if (user != null) {
            // 1: check if successful attempt is between windowStart - today; short circut
            boolean hasRecentSuccess =
                    loginRepository.existsRecentSuccessByUser(user.getId(), windowStart);
            // count failed attempts
            int failedSoFar = hasRecentSuccess ? 0
                    : loginRepository.countFailedByUserSince(user.getId(), windowStart);

            // add current login attempt
            int newCount = failedSoFar + 1;
            if (newCount >= maxAttempts) {
                overLimit = true;
                lockUserIfConfigured(user, lockoutDuration);
            }
        } else {
            // anonymouse/IP based throttling
            String ip = meta != null ? meta.getIpAddress() : null;
            if (ip != null && !ip.isBlank()) {
                boolean hasRecentSuccess = loginRepository.existsRecentSuccessByIp(ip, windowStart);

                int failedSoFar = hasRecentSuccess ? 0
                        : loginRepository.countFailedByIpSince(ip, windowStart);

                int newCount = failedSoFar + 1;
                if (newCount >= maxAttempts) {
                    overLimit = true;
                    // For anonymous, you could insert an IP-based temp lock in cache (recommended)
                    // or record a synthetic lock row with a null user
                    // here we just mark overLimit and proceed to throw
                }
            }
        }

        // always persist the audit row.
        Login attempt = Login.failure(user,
                overLimit ? REASON_TOO_MANY_ATTEMPTS : REASON_INCORRECT_CREDENTIALS, meta,
                authMethod, context, newDevice, null);

        // ensures it survives even if we throw right after
        saveAttemptRequiresNew(attempt);

        if (overLimit) {
            throw new ApiRequestException(REASON_TOO_MANY_ATTEMPTS, HttpStatus.TOO_MANY_REQUESTS);
        }

        throw new ApiRequestException(REASON_INCORRECT_CREDENTIALS, HttpStatus.BAD_REQUEST);
    }

    private void handleEscalationForLockedUser(User user) {
        Instant now = Instant.now();

        // atomic bump
        int updated = lEventRepository.bumpFailedDuringLock(user.getId(), now);
        if (updated == 0) {
            return;
        }

        // re-read
        LockedUser lock = lEventRepository.findActiveLock(user.getId(), now).orElse(null);
        if (lock == null) {
            return;
        }

        int count = lock.getFailedDuringLockCount();
        boolean shouldEscalate = (count % ESCALATE_EVERY_N) == 0;
        if (!shouldEscalate) {
            return;
        }

        // compute next extension
        int level = lock.getEscalationLevel();
        Duration extension = BASE_EXTENSION.multipliedBy(1l << (level - 1)); // 5m, 10m, 20m, etc
        if (extension.compareTo(MAX_LOCK_CAP) > 0) {
            extension = MAX_LOCK_CAP;
        }

        Instant newEnd = lock.getLockoutEnd().plus(extension);
        // don't exceed abs cap from now
        Instant hardCapFromNow = now.plus(MAX_LOCK_CAP);
        if (newEnd.isAfter(hardCapFromNow)) {
            newEnd = hardCapFromNow;
        }

        lock.setEscalationLevel(level);
        lock.setLockoutEnd(newEnd);
        lEventRepository.save(lock);
    }

    /**
     * save attempt in an independent tx so isn't rolled back
     * 
     * @param attempt the {@link Login}
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAttemptRequiresNew(Login attempt) {
        loginRepository.save(attempt);
    }

    private void lockUserIfConfigured(User user, Duration lockoutDuration) {
        if (lockoutDuration == null) {
            // policy says no lockout; do nothing just return
            return;
        }

        LockedUser lUser =
                LockedUser.lock(user, LOCKED_ACCOUNT_AFTER_N_ATTEMPTS, lockoutDuration, false);
        lEventRepository.save(lUser);
        // update user status (if your domain allows it)
        user.handleUserStatus(UserStatus.LOCKED);
        userRepository.save(user);
    }

    private LoginPolicy resolveLoginPolicy(User user) {
        // user not anonymous and has a policy attach to it
        if (user != null && user.getLoginPolicy() != null) {
            return user.getLoginPolicy();
        }

        return lPolicyRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new IllegalStateException("No default policy found."));
    }

    private boolean isActiveLock(User user) {
        Instant now = Instant.now();
        return lEventRepository.existsByUserIdAndLockoutEndAfter(user.getId(), now);
    }

    @Override
    @Transactional
    public void success(User user, boolean isNewDevice, AuthMedium authMethod, LoginContext context,
            RequestMetadata meta) {
        Login attempt = Login.success(user, meta, authMethod, context, isNewDevice);
        loginRepository.save(attempt);
    }
}
