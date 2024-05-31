package dev.twiceb.userservice.controller.rest;

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

import static dev.twiceb.common.constants.ErrorMessage.ACCOUNT_ALREADY_VERIFIED;
import static dev.twiceb.common.constants.ErrorMessage.ACTIVATION_CODE_EXPIRED;
import static dev.twiceb.common.constants.ErrorMessage.EMAIL_ALREADY_TAKEN;
import static dev.twiceb.common.constants.ErrorMessage.USER_NOT_FOUND;
import static dev.twiceb.common.constants.PathConstants.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.twiceb.common.dto.response.ApiErrorResponse;
import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.util.TestConstants;
import dev.twiceb.userservice.dto.request.ProcessEmailRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(value = { "/sql-test/clear-user-db.sql",
        "/sql-test/populate-user-db.sql" }, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = { "/sql-test/clear-user-db.sql" }, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    private RegistrationRequest registrationRequest;

    @BeforeEach
    public void init() {
        registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail(TestConstants.USER_EMAIL);
        registrationRequest.setFirstName(TestConstants.USER_FIRST_NAME);
        registrationRequest.setLastName(TestConstants.USER_LAST_NAME);
        registrationRequest.setPassword(TestConstants.USER_PASSWORD);
        registrationRequest.setPasswordConfirm(TestConstants.USER_PASSWORD);
    }

    @Test
    @DisplayName("[201] POST /ui/v1/auth/registration/check - Check email")
    public void checkEmail() throws Exception {
        String jsonResponse = mockMvc.perform(post(UI_V1_AUTH + REGISTRATION_CHECK)
                .content(mapper.writeValueAsString(registrationRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GenericResponse response = mapper.readValue(jsonResponse, GenericResponse.class);

        assertThat(response).isInstanceOf(GenericResponse.class);
        // the message is dynamic so we check the first sentence.
        assertThat(response.getMessage()).contains("User created successfully.");
    }

    @Test
    @DisplayName("[403] POST /ui/v1/auth/registration/check - Should user email is exist")
    public void checkEmail_ShouldUserEmailIsExist() throws Exception {
        registrationRequest.setEmail(TestConstants.SAME_USER_EMAIL);
        String jsonResponse = mockMvc.perform(post(UI_V1_AUTH + REGISTRATION_CHECK)
                .content(mapper.writeValueAsString(registrationRequest))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        GenericResponse response = mapper.readValue(jsonResponse, GenericResponse.class);

        assertThat(response).isInstanceOf(GenericResponse.class);
        assertThat(response.getMessage()).isEqualTo(EMAIL_ALREADY_TAKEN);
    }

    @Test
    @DisplayName("[200] POST /ui/v1/auth/registration/code - Send registration code")
    public void sendRegistrationCode() throws Exception {
        ProcessEmailRequest request = new ProcessEmailRequest();
        request.setEmail(TestConstants.SAME_USER_EMAIL);
        String jsonResponse = mockMvc.perform(post(UI_V1_AUTH + REGISTRATION_CODE)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        GenericResponse response = mapper.readValue(jsonResponse, GenericResponse.class);

        assertThat(response).isInstanceOf(GenericResponse.class);
        assertThat(response.getMessage()).isEqualTo("Activation Code was sent to your email.");
    }

    @Test
    @DisplayName("[400] POST /ui/v1/auth/registration/code - Should user be verified already")
    public void sendRegistrationCode_ShouldUserIsVerified() throws Exception {
        ProcessEmailRequest request = new ProcessEmailRequest();
        request.setEmail("jane.smith@example.com");
        String jsonResponse = mockMvc.perform(post(UI_V1_AUTH + REGISTRATION_CODE)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ApiErrorResponse response = mapper.readValue(jsonResponse, ApiErrorResponse.class);

        assertThat(response).isInstanceOf(ApiErrorResponse.class);
        assertThat(response.getMessage()).isEqualTo(ACCOUNT_ALREADY_VERIFIED);
    }

    @Test
    @DisplayName("[404] POST /ui/v1/auth/registration/code - User not found")
    public void sendRegistrationCode_ShouldUserNotFound() throws Exception {
        ProcessEmailRequest request = new ProcessEmailRequest();
        request.setEmail(TestConstants.NOT_VALID_EMAIL);
        String jsonResponse = mockMvc.perform(post(UI_V1_AUTH + REGISTRATION_CODE)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ApiErrorResponse response = mapper.readValue(jsonResponse, ApiErrorResponse.class);

        assertThat(response).isInstanceOf(ApiErrorResponse.class);
        assertThat(response.getMessage()).isEqualTo(USER_NOT_FOUND);
    }

    @Test
    @DisplayName("[200] GET /ui/v1/auth/registration/activate/{code} - Check registration code")
    public void checkRegistrationCode() throws Exception {
        String code = "dGhpcyBpcyBhIHRlc3Qgc3RyaW5n";
        mockMvc.perform(get(UI_V1_AUTH + REGISTRATION_ACTIVATE_CODE, code))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.deviceToken", isA(String.class)))
                .andExpect(jsonPath("$.message", is(
                        "You've activated your account and you're now ready to use it! Try and login to access your account.")));
    }

    @Test
    @DisplayName("[410] GET /ui/v1/auth/registration/activate/{code} - registration code expired")
    public void checkRegistrationCode_ShouldItExpire() throws Exception {
        String code = "L3ZiU7TsQHuhK8JvGvW4Tw";
        mockMvc.perform(get(UI_V1_AUTH + REGISTRATION_ACTIVATE_CODE, code))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.message", is(ACTIVATION_CODE_EXPIRED)));
    }

}
