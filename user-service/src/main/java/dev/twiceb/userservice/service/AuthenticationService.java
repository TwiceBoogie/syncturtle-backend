package dev.twiceb.userservice.service;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.exception.NoRollbackApiRequestException;
import dev.twiceb.common.records.AuthenticatedUserRecord;
import dev.twiceb.common.records.MagicCodeRecord;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.MagicCodeRequest;
import dev.twiceb.userservice.dto.request.PasswordResetRequest;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.projection.UserPrincipalProjection;
import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.UUID;

public interface AuthenticationService {
    /**
     * Retrieves the user ID from the HTTP request headers.
     * 
     * @return The user ID extracts from the request headers.
     */
    UUID getAuthenticatedUserId();

    /**
     * Retrieves the authenticated user based on the user ID extracted from the request.
     *
     * @return The authenticated user.
     * @throws ApiRequestException If the user corresponding to the authenticated user ID is not
     *         found.
     */
    User getAuthenticatedUser();

    /**
     * Retrieves the user principal projection based on the specified email.
     *
     * @param email The email of the user.
     * @return The user principal projection containing essential user details.
     * @throws ApiRequestException If the user corresponding to the email is not found.
     */
    UserPrincipalProjection getUserPrincipleByEmail(String email);

    MagicCodeRecord checkEmail(String email, BindingResult bindingResult);

    String generateMagicCode(String email, BindingResult bindingResult);

    AuthenticatedUserRecord magicLogin(MagicCodeRequest request, BindingResult bindingResult);

    /**
     * Process the login request, authenticates user, and generates a JWT Token upon successfully
     * login. if auth fails due to incorrect creds or any other reason, appropriate exceptions are
     * thrown.
     *
     * @param request The authentication request containing the username and password.
     * @param bindingResult The result of the binding process between the authentication request and
     *        the controller method.
     *
     * @return A map containing the authenticated user and a JWT Token.
     *
     * @throws ApiRequestException If the user is not found or the user is not verified.
     * @throws NoRollbackApiRequestException If the user's deviceKey is not valid or the user is
     *         locked.
     */
    AuthenticatedUserRecord login(AuthenticationRequest request, BindingResult bindingResult);

    AuthenticatedUserRecord getUserByToken();

    /**
     * Sends an email containing the username associated with the specified email address. If the
     * email address is registered, an email with the username is sent. Otherwise, throws an
     * exception.
     *
     * @param email The email address for which to retrieve the username.
     * @param bindingResult The result of the binding process between the email parameter and the
     *        controller method.
     *
     * @return A success message indicating that the email containing the username has been sent.
     *
     * @throws ApiRequestException If no user is found with the specified email address.
     */
    Map<String, String> forgotUsername(String email, BindingResult bindingResult);

    /**
     * Initiates the process of resetting a user's password by sending an OTP (One-Time Password)
     * via email. The user is identified by their email address. Step 1 of resetting password
     *
     * @param email The email address of the user requesting a password reset.
     * @param bindingResult The binding result for validating input parameters.
     * @return A map containing a message confirming the OTP email has been sent.
     * @throws ApiRequestException If the user with the provided email address is not found.
     */
    Map<String, String> forgotPassword(String email, BindingResult bindingResult);

    /**
     * Verifies the provided one-time password (OTP) and creates a password reset token. If the OTP
     * is valid, it expires and a password reset token is generated and sent to the user's email.
     * Step 2 of resetting password
     *
     * @param otp The one-time password provided by the user for verification.
     * @param bindingResult The result of the binding process between the OTP parameter and the
     *        controller method.
     *
     * @return A success message indicating that the OTP has been verified and a password reset
     *         token has been sent.
     *
     * @throws ApiRequestException If the provided OTP is invalid or has expired.
     */
    Map<String, String> verifyOtp(String otp, BindingResult bindingResult);

    /**
     * The final step of the reset password process. It verifies the provided token and validates
     * the new password.
     * 
     * @param request The new password provided by the user.
     * @param token The reset token provided by the user for verification.
     * @param bindingResult The result of the binging process between the request and the controller
     *        method.
     * @return
     */
    Map<String, String> resetPassword(PasswordResetRequest request, String token,
            BindingResult bindingResult);

    /**
     * Verifies a new device using the provided device verification code after an unknown login from
     * new device. If trust is false, the user did not make the login request and will lock the user
     * and start the password reset process.
     *
     * @param deviceVerificationCode The device verification code sent to the user's email.
     * @param trust Indicates whether the device should be trusted.
     * @return A map containing the user information, JWT token, and device token if the
     *         verification is successful.
     */
    AuthenticatedUserRecord newDeviceVerification(String deviceVerificationCode, boolean trust);
}
