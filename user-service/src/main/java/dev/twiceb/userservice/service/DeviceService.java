package dev.twiceb.userservice.service;

import dev.twiceb.userservice.model.User;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * DeviceService provides operations for device management, including
 * verification and email notifications.
 */
public interface DeviceService {

    /**
     * Verifies if the device key provided matches the stored device key for the
     * user.
     *
     * @param userId          The ID of the user.
     * @param hashedDeviceKey The hashed device key to be verified.
     * @return true if the device key matches; false otherwise.
     */
    boolean verifyDevice(UUID userId, String hashedDeviceKey);

    /**
     * Retrieves and validates the device verification code provided.
     *
     * @param deviceVerificationCode The device verification code to be validated.
     * @return The user associated with the device verification code if valid.
     */
    Optional<User> retrieveAndValidateDeviceVerificationCode(String deviceVerificationCode);

    /**
     * Verifies a new device for the user using the provided device key. Also
     * updates the user status to active
     *
     *
     * @param user      The user to verify the new device for.
     * @param deviceKey The new deviceKey
     * @return The updated user with the new device verified.
     */
    User processNewDevice(User user, String deviceKey);

    /**
     * Sends a device verification email to the user with the provided details.
     *
     * @param user          The user to send the verification email to.
     * @param customHeaders Custom headers containing additional information such
     *                      as the user's IP address.
     */
    void sendDeviceVerificationEmail(User user, Map<String, String> customHeaders);

    /**
     * Verifies if a device verification code has been sent to the user.
     * - If the hashed code is an empty string, it indicates that the code has
     * already been used, and the method returns false.
     * - If the code has expired, the expiration time is extended, and the method
     * returns true.
     *
     * @param userId      The ID of the authenticated user.
     * @param currentTime The current time for validation purposes.
     * @return true if the device verification code is valid or has been reissued;
     *         false if the code has already been used or null.
     */
    boolean isDeviceVerificationCodeSent(UUID userId, LocalDateTime currentTime);
}
