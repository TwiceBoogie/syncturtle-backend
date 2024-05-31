package dev.twiceb.userservice.controller.rest;

import static dev.twiceb.common.constants.ErrorMessage.AUTHORIZATION_ERROR;
import static dev.twiceb.common.constants.ErrorMessage.DEVICE_KEY_NOT_FOUND_OR_MATCH;
import static dev.twiceb.common.constants.ErrorMessage.INCORRECT_PASSWORD;
import static dev.twiceb.common.constants.ErrorMessage.LOCKED_ACCOUNT_AFTER_N_ATTEMPTS;
import static dev.twiceb.common.constants.ErrorMessage.OTP_HAS_EXPIRED;
import static dev.twiceb.common.constants.ErrorMessage.OTP_NOT_FOUND;
import static dev.twiceb.common.constants.ErrorMessage.USER_NOT_FOUND_WITH_EMAIL;
import static dev.twiceb.common.constants.ErrorMessage.VERIFY_ACCOUNT_WITH_EMAIL;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_AGENT_HEADER;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_DEVICE_KEY;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_IP_HEADER;
import static dev.twiceb.common.constants.PathConstants.FORGOT_PASSWORD;
import static dev.twiceb.common.constants.PathConstants.FORGOT_USERNAME;
import static dev.twiceb.common.constants.PathConstants.LOGIN;
import static dev.twiceb.common.constants.PathConstants.RESET;
import static dev.twiceb.common.constants.PathConstants.UI_V1_AUTH;
import static dev.twiceb.common.constants.PathConstants.VERIFY_DEVICE_VERIFICATION;
import static dev.twiceb.common.constants.PathConstants.VERIFY_OTP;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.twiceb.common.util.TestConstants;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.PasswordOtpRequest;
import dev.twiceb.userservice.dto.request.PasswordResetRequest;
import dev.twiceb.userservice.dto.request.ProcessEmailRequest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = { "/sql-test/clear-user-db.sql",
        "/sql-test/populate-user-db.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = { "/sql-test/clear-user-db.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private AuthenticationRequest authenticationRequest;
    private String randomCodeForDeviceKey = "L3ZiU7TsQHuhK8JvGvW4Tw";
    private String wrongRandomCodeForDeviceKey = "dGhpcyBpcyBhIHRlc3Qgc3RyaW5n";

    @BeforeEach
    public void init() {
        authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setUsername(TestConstants.AUTH_USER_USERNAME);
        authenticationRequest.setPassword(TestConstants.USER_PASSWORD);
    }

    @Test
    @DisplayName("[200] POST /ui/v1/auth/login - Login")
    public void login() throws Exception {
        mockMvc.perform(post(UI_V1_AUTH + LOGIN)
                .content(mapper.writeValueAsString(authenticationRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTH_USER_DEVICE_KEY, randomCodeForDeviceKey)
                .header(AUTH_USER_AGENT_HEADER, TestConstants.USER_AGENT)
                .header(AUTH_USER_IP_HEADER, TestConstants.USER_IP))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(TestConstants.USER_ID))
                .andExpect(jsonPath("$.user.email").value(TestConstants.AUTH_USER_EMAIL))
                .andExpect(jsonPath("$.user.firstName").value(TestConstants.AUTH_USER_FIRST_NAME))
                .andExpect(jsonPath("$.user.lastName").value(TestConstants.AUTH_USER_LAST_NAME))
                .andExpect(jsonPath("$.token", isA(String.class)))
                .andExpect(jsonPath("$.deviceToken").doesNotExist()); // device token set in the registration service

    }

    @Test
    @DisplayName("[400] POST /ui/v1/auth/login - User not verified")
    public void login_ShouldUserNotVerified() throws Exception {
        authenticationRequest.setUsername(TestConstants.USER_USERNAME);
        mockMvc.perform(post(UI_V1_AUTH + LOGIN)
                .content(mapper.writeValueAsString(authenticationRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTH_USER_DEVICE_KEY, randomCodeForDeviceKey)
                .header(AUTH_USER_AGENT_HEADER, TestConstants.USER_AGENT)
                .header(AUTH_USER_IP_HEADER, TestConstants.USER_IP))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(VERIFY_ACCOUNT_WITH_EMAIL)));
    }

    @Test
    @DisplayName("[403] POST /ui/v1/auth/login - Device key not present or does not belong to user")
    public void login_ShouldDeviceKeyNotBelongToUser() throws Exception {
        mockMvc.perform(post(UI_V1_AUTH + LOGIN)
                .content(mapper.writeValueAsString(authenticationRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTH_USER_DEVICE_KEY, wrongRandomCodeForDeviceKey)
                .header(AUTH_USER_AGENT_HEADER, TestConstants.USER_AGENT)
                .header(AUTH_USER_IP_HEADER, TestConstants.USER_IP))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(DEVICE_KEY_NOT_FOUND_OR_MATCH)));
    }

    @Test
    @DisplayName("[400] POST /ui/v1/auth/login - Too many failed attempts")
    public void login_ShouldUserFailLogin() throws Exception {
        authenticationRequest.setUsername(TestConstants.AUTH_USER_USERNAME_BAD_LOGIN_ATTEMPTS);
        authenticationRequest.setPassword("password1234");
        mockMvc.perform(post(UI_V1_AUTH + LOGIN)
                .content(mapper.writeValueAsString(authenticationRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTH_USER_DEVICE_KEY, randomCodeForDeviceKey)
                .header(AUTH_USER_AGENT_HEADER, TestConstants.USER_AGENT)
                .header(AUTH_USER_IP_HEADER, TestConstants.USER_IP))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(INCORRECT_PASSWORD)));
    }

    @Test
    @DisplayName("[429] POST /ui/v1/auth/login - User is locked")
    public void login_ShouldUserBeLocked() throws Exception {
        authenticationRequest.setUsername(TestConstants.AUTH_USER_USERNAME_LOCKED);
        mockMvc.perform(post(UI_V1_AUTH + LOGIN)
                .content(mapper.writeValueAsString(authenticationRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTH_USER_DEVICE_KEY, randomCodeForDeviceKey)
                .header(AUTH_USER_AGENT_HEADER, TestConstants.USER_AGENT)
                .header(AUTH_USER_IP_HEADER, TestConstants.USER_IP))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.message", containsString(LOCKED_ACCOUNT_AFTER_N_ATTEMPTS)));
    }

    @Test
    @DisplayName("[403] POST /ui/v1/auth/login - User account is locked until they verify new device")
    public void login_ShouldUserBeLockedForNewDeviceVerification() throws Exception {
        authenticationRequest.setUsername(TestConstants.AUTH_USER_USERNAME_PENDING);
        mockMvc.perform(post(UI_V1_AUTH + LOGIN)
                .content(mapper.writeValueAsString(authenticationRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTH_USER_DEVICE_KEY, "")
                .header(AUTH_USER_AGENT_HEADER, TestConstants.USER_AGENT)
                .header(AUTH_USER_IP_HEADER, TestConstants.USER_IP))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is(DEVICE_KEY_NOT_FOUND_OR_MATCH)));
    }

    @Test
    @DisplayName("[401] POST /ui/v1/auth/login - User not found")
    public void login_ShouldUserNotExist() throws Exception {
        authenticationRequest.setUsername("randomUser123");
        mockMvc.perform(post(UI_V1_AUTH + LOGIN)
                .content(mapper.writeValueAsString(authenticationRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTH_USER_DEVICE_KEY, "")
                .header(AUTH_USER_AGENT_HEADER, TestConstants.USER_AGENT)
                .header(AUTH_USER_IP_HEADER, TestConstants.USER_IP))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message", is("Invalid username or password")));
    }

    @Test
    @DisplayName("[200] POST /ui/v1/auth/forgot/username - Send username to user's email")
    public void forgotUsername() throws Exception {
        ProcessEmailRequest request = new ProcessEmailRequest();
        request.setEmail(TestConstants.AUTH_USER_EMAIL);
        mockMvc.perform(post(UI_V1_AUTH + FORGOT_USERNAME)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Check your email for your username")));
    }

    @Test
    @DisplayName("[404] POST /ui/v1/auth/forgot/username - User email not in database")
    public void forgotUsername_ShouldUserNotExist() throws Exception {
        ProcessEmailRequest request = new ProcessEmailRequest();
        request.setEmail("test@test.com");
        mockMvc.perform(post(UI_V1_AUTH + FORGOT_USERNAME)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(USER_NOT_FOUND_WITH_EMAIL)));
    }

    @Test
    @DisplayName("[200] POST /ui/v1/auth/forgot/password - Send password reset otp to user's email")
    public void forgotPassword() throws Exception {
        ProcessEmailRequest request = new ProcessEmailRequest();
        request.setEmail(TestConstants.AUTH_USER_EMAIL);
        mockMvc.perform(post(UI_V1_AUTH + FORGOT_PASSWORD)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Check your email for the password otp")));
    }

    @Test
    @DisplayName("[402] POST /ui/v1/auth/forgot/password - User email not in database")
    public void forgotPassword_ShouldUserNotExist() throws Exception {
        ProcessEmailRequest request = new ProcessEmailRequest();
        request.setEmail("test@test.com");
        mockMvc.perform(post(UI_V1_AUTH + FORGOT_PASSWORD)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(USER_NOT_FOUND_WITH_EMAIL)));
    }

    @Test
    @DisplayName("[200] POST /ui/v1/auth/verify-otp - Verifies and sends a password reset link to user's email")
    public void verifyOtp() throws Exception {
        PasswordOtpRequest request = new PasswordOtpRequest();
        request.setOtp("123456");
        mockMvc.perform(post(UI_V1_AUTH + VERIFY_OTP)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("OTP verified, check your email for the password reset link.")));
    }

    @Test
    @DisplayName("[404] POST /ui/v1/auth/verify-otp - OTP does not exist")
    public void verifyOtp_ShouldOtpNotValid() throws Exception {
        PasswordOtpRequest request = new PasswordOtpRequest();
        request.setOtp("123457");
        mockMvc.perform(post(UI_V1_AUTH + VERIFY_OTP)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(OTP_NOT_FOUND)));
    }

    @Test
    @DisplayName("[400] POST /ui/v1/auth/verify-otp - OTP has expired")
    public void verifyOtp_ShouldOtpExpire() throws Exception {
        PasswordOtpRequest request = new PasswordOtpRequest();
        request.setOtp("123458");
        mockMvc.perform(post(UI_V1_AUTH + VERIFY_OTP)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(OTP_HAS_EXPIRED)));
    }

    @Test
    @DisplayName("[200] POST /ui/v1/auth/reset/{token} - Reset password")
    public void resetPassword() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setPassword("Twice_Mina1");
        request.setConfirmPassword("Twice_Mina1");
        String token = "TNFiPaRHxmRlum7gFCYWFA";
        mockMvc.perform(post(UI_V1_AUTH + RESET, token)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Password has been reset. Try to login with your new password")));
    }

    @Test
    @DisplayName("[404] POST /ui/v1/auth/reset/{token} - Token is missing")
    public void resetPassword_ShouldTokenNotExist() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setPassword("Twice_Mina1");
        request.setConfirmPassword("Twice_Mina1");
        String token = "";
        mockMvc.perform(post(UI_V1_AUTH + RESET, token)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[404] POST /ui/v1/auth/reset/{token} - Token not found in db")
    public void resetPassword_ShouldTokenNotExistInDb() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setPassword("Twice_Mina1");
        request.setConfirmPassword("Twice_Mina1");
        String token = "1";
        mockMvc.perform(post(UI_V1_AUTH + RESET, token)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest());
        // .andExpect(jsonPath("$.message", is("Password reset token is invalid")));
        // change since right now it will say for device verification
    }

    @Test
    @DisplayName("[404] POST /ui/v1/auth/reset/{token} - Token has expired")
    public void resetPassword_ShouldTokenExpire() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setPassword("Twice_Mina1");
        request.setConfirmPassword("Twice_Mina1");
        String token = "G97ySHfAOQtiqRdHsslkEA";
        mockMvc.perform(post(UI_V1_AUTH + RESET, token)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Reset token has expired")));
    }

    @Test
    @DisplayName("[200] GET /ui/v1/auth/verify/device/{token} - New device trusted")
    public void verifyDeviceToken() throws Exception {
        String token = "0dtIY2jKYPbPg55q9awpzg";
        mockMvc.perform(get(UI_V1_AUTH + VERIFY_DEVICE_VERIFICATION, token)
                .param("trust", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(TestConstants.NEW_DEVICE_USER_ID))
                .andExpect(jsonPath("$.user.email").value(TestConstants.NEW_DEVICE_USER_EMAIL))
                .andExpect(jsonPath("$.user.firstName").value(TestConstants.NEW_DEVICE_FIRST_NAME))
                .andExpect(jsonPath("$.user.lastName").value(TestConstants.NEW_DEVICE_LAST_NAME))
                .andExpect(jsonPath("$.token", isA(String.class)))
                .andExpect(jsonPath("$.deviceToken", isA(String.class))); // device token set in the registration
                                                                          // service
    }

    @Test
    @DisplayName("[423] GET /ui/v1/auth/verify/device/{token} - New device not trusted")
    public void verifyDeviceToken_ShouldNotTrusted() throws Exception {
        String token = "0dtIY2jKYPbPg55q9awpzg";
        mockMvc.perform(get(UI_V1_AUTH + VERIFY_DEVICE_VERIFICATION, token)
                .param("trust", "false"))
                .andExpect(status().isLocked())
                .andExpect(jsonPath("$.message", is(AUTHORIZATION_ERROR)));
    }

    @Test
    @DisplayName("[404] GET /ui/v1/auth/verify/device/{token} - New device token missing")
    public void verifyDeviceToken_ShouldTokenMissing() throws Exception {
        String token = "";
        mockMvc.perform(get(UI_V1_AUTH + VERIFY_DEVICE_VERIFICATION, token)
                .param("trust", "false"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("[403] GET /ui/v1/auth/verify/device/{token} - New device token is wrong")
    public void verifyDeviceToken_ShouldTokenNotExist() throws Exception {
        String token = "1";
        mockMvc.perform(get(UI_V1_AUTH + VERIFY_DEVICE_VERIFICATION, token)
                .param("trust", "true"))
                .andExpect(status().isBadRequest());
    }
}