package dev.twiceb.userservice.service;

import dev.twiceb.common.exception.NoRollbackApiRequestException;
import dev.twiceb.userservice.model.User;

import java.time.LocalDateTime;
import java.util.Map;

public interface LoginAttemptService {
    void generateLoginAttempt(boolean success, boolean newDevice, User user, String ipAddress);

    /**
     * Handles the login attempts for a user account.
     * It checks if there are any successful attempts starting from the specified start date.
     * If there are any successful attempts, it won't count any failed attempts
     * within the period from the start date to the current time and those will be ignored.
     *
     * @param isPasswordMatch boolean from previous password check
     * @param user The user account being verified.
     * @param customHeaders The custom headers containing additional information such as the user's IP address.
     *
     * @throws NoRollbackApiRequestException If the provided password is incorrect, indicating a failed login attempt.
     *                                     If the maximum number of login attempts is reached, the user's account will be locked.
     */
    void handleLoginAttempt(boolean isPasswordMatch, User user, Map<String, String> customHeaders);

    void updateLoginAttempt(Long userId);

    User lockUser(User user, String reason);

    /**
     * Handles the case where the user's account is locked.
     * If the lockout duration has not yet elapsed, a {@link NoRollbackApiRequestException} is thrown.
     * Otherwise, the user's status is updated to active.
     *
     * @param user          The user whose account is locked.
     * @param customHeaders The custom headers containing the user's IP address and other information.
     * @param currentTime   The current time used to calculate the lockout duration.
     * @throws NoRollbackApiRequestException If the lockout duration has not yet elapsed.
     */
    void handleLockedUser(User user, Map<String, String> customHeaders, LocalDateTime currentTime);
}
