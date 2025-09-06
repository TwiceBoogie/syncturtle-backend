package dev.twiceb.userservice.service;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.userservice.domain.model.PasswordResetToken;
import dev.twiceb.userservice.domain.model.User;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for handling password reset operations.
 */
public interface PasswordResetService {

    /**
     * Creates and sends a password reset OTP email to the specified user. This method generates an
     * OTP (One-Time Password) entity, and then sends an email to the user with the OTP.
     *
     * @param user The user to whom the password reset OTP email will be sent.
     * @return A map containing a message indicating the status of the operation.
     */
    Map<String, String> createAndSendPasswordResetOtpEmail(User user);

    /**
     * Verifies the provided OTP against the stored hashed OTP and validates the token. This method
     * checks if the provided OTP matches the stored hashed OTP and if the token is valid.
     *
     * @param otp The hashed version of the OTP to be verified with the one in
     * @return A map containing a message indicating the status of the operation.
     * @throws ApiRequestException If the OTP verification fails.
     */
    Map<String, String> verifyOtp(String otp);

    /**
     * Validates and expires the provided password reset token. This method checks if the token is
     * valid and not expired, and then marks it as expired.
     *
     * @param token The password reset token to be validated and expired.
     * @return The PasswordResetToken entity after being validated and marked as expired.
     * @throws ApiRequestException If the token validation fails or if the token is already expired.
     */
    Optional<PasswordResetToken> validateAndExpirePasswordResetToken(String token);
}
