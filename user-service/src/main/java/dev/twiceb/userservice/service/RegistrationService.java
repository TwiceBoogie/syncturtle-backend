package dev.twiceb.userservice.service;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.userservice.dto.request.*;
import org.springframework.validation.BindingResult;

import java.util.Map;

public interface RegistrationService {
    Map<String, String> registration(RegistrationRequest request, BindingResult bindingResult);

    Map<String, String> sendRegistrationCode(String email, BindingResult bindingResult);

    Map<String, Object> checkRegistrationCode(String code);

//    /**
//     * Sends an email containing the username associated with the specified email address.
//     * If the email address is registered, an email with the username is sent. Otherwise, throws an exception.
//     *
//     * @param email The email address for which to retrieve the username.
//     * @param bindingResult The result of the binding process between the email parameter and the controller method.
//     *
//     * @return A success message indicating that the email containing the username has been sent.
//     *
//     * @throws ApiRequestException If no user is found with the specified email address.
//     */
//    Map<String, String> forgotUsername(String email, BindingResult bindingResult);
//
//    /**
//     * Initiates the process of resetting a user's password by sending an OTP (One-Time Password) via email.
//     * The user is identified by their email address.
//     *
//     * @param email The email address of the user requesting a password reset.
//     * @param bindingResult The binding result for validating input parameters.
//     * @return A map containing a message confirming the OTP email has been sent.
//     * @throws ApiRequestException If the user with the provided email address is not found.
//     */
//    Map<String, String> forgotPassword(String email, BindingResult bindingResult);
//
//    /**
//     * Verifies the provided one-time password (OTP) and creates a password reset token.
//     * If the OTP is valid, it expires and a password reset token is generated and sent to the user's email.
//     *
//     * @param otp The one-time password provided by the user for verification.
//     * @param bindingResult The result of the binding process between the OTP parameter and the controller method.
//     *
//     * @return A success message indicating that the OTP has been verified and a password reset token has been sent.
//     *
//     * @throws ApiRequestException If the provided OTP is invalid or has expired.
//     */
//    Map<String, String> verifyOtp(String otp, BindingResult bindingResult);
//
//    Map<String, String> resetPassword(PasswordResetRequest request, String token, BindingResult bindingResult);
}
