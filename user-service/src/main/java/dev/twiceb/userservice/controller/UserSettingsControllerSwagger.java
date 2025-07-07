package dev.twiceb.userservice.controller;

import dev.twiceb.common.dto.request.SettingsRequestGroup;
import dev.twiceb.common.dto.response.ApiErrorResponse;
import dev.twiceb.common.util.ApiResponseExamples;
import dev.twiceb.userservice.dto.request.SettingsRequest;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import dev.twiceb.userservice.dto.response.ProfilePicResponse;
import dev.twiceb.userservice.dto.response.UserPhoneResponse;
import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.Parameter;
// import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static dev.twiceb.common.constants.ErrorMessage.*;
import static dev.twiceb.common.constants.PathConstants.*;

@SecurityScheme(type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", name = "Authorization", in = SecuritySchemeIn.HEADER)
@SecurityRequirement(name = "Authorization")
@ApiResponse(responseCode = "401", description = JWT_TOKEN_EXPIRED, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_JWT)))
public interface UserSettingsControllerSwagger {

        @PutMapping(USERNAME)
        @Operation(summary = "Update user's username", responses = {
                        @ApiResponse(responseCode = "200", description = "Successful response", content = @Content(mediaType = "*/*", schema = @Schema(type = "string", implementation = String.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_INPUT_ERROR))
                        }),
                        @ApiResponse(responseCode = "409", description = USERNAME_ALREADY_TAKEN, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_USERNAME_TAKEN_EXAMPLE)))
        })
        ResponseEntity<String> updateUsername(
                        @Validated(SettingsRequestGroup.UsernameUpdateGroup.class) @RequestBody SettingsRequest request,
                        BindingResult bindingResult);

        @PutMapping(EMAIL)
        @Operation(summary = "Update user's email", responses = {
                        @ApiResponse(responseCode = "200", description = "Successful response", content = @Content(mediaType = "*/*", schema = @Schema(type = "string", implementation = String.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_INPUT_ERROR))
                        }),
                        @ApiResponse(responseCode = "409", description = EMAIL_ALREADY_TAKEN, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_EMAIL_TAKEN_EXAMPLE)))
        })
        ResponseEntity<AuthenticationResponse> updateEmail(
                        @Validated(SettingsRequestGroup.EmailUpdateGroup.class) @RequestBody SettingsRequest request,
                        BindingResult bindingResult);

        @PutMapping(PHONE)
        @Operation(summary = "Update user's phone", responses = {
                        @ApiResponse(responseCode = "200", description = "Successful response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserPhoneResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_INPUT_ERROR))
                        })
        })
        ResponseEntity<UserPhoneResponse> updatePhone(
                        @Validated(SettingsRequestGroup.PhoneUpdateGroup.class) @RequestBody SettingsRequest request,
                        BindingResult bindingResult);

        @PutMapping(GENDER)
        @Operation(summary = "Update user's gender", responses = {
                        @ApiResponse(responseCode = "200", description = "Successful response", content = @Content(mediaType = "application/json", schema = @Schema(type = "string", implementation = String.class))),
                        @ApiResponse(responseCode = "400", description = "Validation error", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_INPUT_ERROR))
                        })
        })
        ResponseEntity<String> updateGender(
                        @Validated(SettingsRequestGroup.GenderUpdateGroup.class) @RequestBody SettingsRequest request,
                        BindingResult bindingResult);

        @PutMapping(SET_AVATAR)
        @Operation(summary = "Update user's profile pic", responses = {
                        @ApiResponse(responseCode = "200", description = "Successful response", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfilePicResponse.class))),
                        @ApiResponse(responseCode = "404", description = "No Resource found", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_NO_RESOURCE))
                        })
        })
        ResponseEntity<ProfilePicResponse> updateProfilePic(@PathVariable Long userProfileId);

        @PostMapping(AVATAR)
        @Operation(summary = "Upload profile pics", responses = {
                        @ApiResponse(responseCode = "200", description = "Successful response", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProfilePicResponse.class)))),
                        @ApiResponse(responseCode = "400", description = "No files provided for upload or more than 10 pics uploaded", content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_FILES))
                        })
        })
        ResponseEntity<List<ProfilePicResponse>> uploadProfilePics(@RequestPart("files") MultipartFile[] files);

        @DeleteMapping(SET_AVATAR)
        @Operation(summary = "Delete profile pic", responses = {
                        @ApiResponse(responseCode = "200", description = "Successful response", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ProfilePicResponse.class)))),
                        @ApiResponse(responseCode = "404", description = NO_RESOURCE_FOUND, content = {
                                        @Content(mediaType = "application/json", schema = @Schema(implementation = ApiErrorResponse.class), examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_NO_RESOURCE))
                        })
        })
        ResponseEntity<List<ProfilePicResponse>> deleteProfilePic(@PathVariable Long userProfileId);
}
