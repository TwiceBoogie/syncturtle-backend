package dev.twiceb.userservice.controller;

import dev.twiceb.common.dto.response.ApiErrorResponse;
import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.util.ApiResponseExamples;
import dev.twiceb.userservice.dto.request.AuthenticationRequest;
import dev.twiceb.userservice.dto.request.PasswordOtpRequest;
import dev.twiceb.userservice.dto.request.PasswordResetRequest;
import dev.twiceb.userservice.dto.request.ProcessEmailRequest;
import dev.twiceb.userservice.dto.response.AuthUserResponse;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.ErrorMessage.USER_NOT_FOUND_WITH_EMAIL;
import static dev.twiceb.common.constants.PathConstants.*;
import static dev.twiceb.common.constants.PathConstants.VERIFY_DEVICE_VERIFICATION;

@SecurityScheme(type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", name = "Authorization", in = SecuritySchemeIn.HEADER)
@SecurityScheme(name = "deviceAuth", // Reference name for device authentication
                type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, paramName = "X-User-DeviceKey" // Header
                                                                                                               // name
                                                                                                               // for
                                                                                                               // device
                                                                                                               // JWT
)
public interface AuthenticationControllerSwagger {

        @PostMapping(LOGIN)
        @ApiResponse(responseCode = "200", description = "Successful request", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = AuthUserResponse.class))
        })
        @ApiResponse(responseCode = "400", description = "Validation error", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_INPUT_ERROR))
        })
        @ApiResponse(responseCode = "401", description = "Invalid Login", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_INVALID_LOGIN))
        })
        @ApiResponse(responseCode = "403", description = "No device key jwt", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_NO_DEVICE_KEY))
        })
        @SecurityRequirement(name = "deviceAuth")
        ResponseEntity<AuthUserResponse> login(@Valid @RequestBody AuthenticationRequest request,
                        BindingResult bindingResult);

        @PostMapping(FORGOT_USERNAME)
        @ApiResponse(responseCode = "200", description = "Successful request", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponse.class))
        })
        @ApiResponse(responseCode = "404", description = USER_NOT_FOUND_WITH_EMAIL, content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_USER_NOT_FOUND_WITH_EMAIL))
        })
        ResponseEntity<GenericResponse> forgotUsername(@Valid @RequestBody ProcessEmailRequest request,
                        BindingResult bindingResult);

        @PostMapping(FORGOT_PASSWORD)
        @ApiResponse(responseCode = "200", description = "Successful request", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponse.class))
        })
        @ApiResponse(responseCode = "404", description = USER_NOT_FOUND_WITH_EMAIL, content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_USER_NOT_FOUND_WITH_EMAIL))
        })
        ResponseEntity<GenericResponse> forgotPassword(@Valid @RequestBody ProcessEmailRequest request,
                        BindingResult bindingResult);

        @PostMapping(VERIFY_OTP)
        @ApiResponse(responseCode = "200", description = "Successful response", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponse.class))
        })
        @ApiResponse(responseCode = "400", description = "Validation error", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_INPUT_ERROR))
        })
        @ApiResponse(responseCode = "404", description = "No Resource found", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_NO_RESOURCE))
        })
        ResponseEntity<GenericResponse> verifyOtp(@Valid @RequestBody PasswordOtpRequest request,
                        BindingResult bindingResult);

        @PostMapping(RESET)
        @ApiResponse(responseCode = "200", description = "Successful response", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = GenericResponse.class))
        })
        @ApiResponse(responseCode = "400", description = "Reset token has expired", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_PW_RESET_TOKEN_EXPIRED))
        })
        @ApiResponse(responseCode = "404", description = "Password reset token is invalid", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_PW_RESET_TOKEN_NOT_FOUND))
        })
        ResponseEntity<GenericResponse> resetPassword(@Valid @RequestBody PasswordResetRequest request,
                        @PathVariable("token") String token,
                        BindingResult bindingResult);

        @GetMapping(VERIFY_DEVICE_VERIFICATION)
        ResponseEntity<AuthenticationResponse> verifyDeviceToken(@PathVariable("token") String token,
                        @RequestParam("trust") boolean trust);
}
