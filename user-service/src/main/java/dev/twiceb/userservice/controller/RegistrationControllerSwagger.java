package dev.twiceb.userservice.controller;

import dev.twiceb.common.dto.response.ApiErrorResponse;
import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.util.ApiResponseExamples;
import dev.twiceb.userservice.dto.request.ProcessEmailRequest;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.dto.response.RegistrationEndResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static dev.twiceb.common.constants.ErrorMessage.*;
import static dev.twiceb.common.constants.PathConstants.*;

public interface RegistrationControllerSwagger {

    @PostMapping(REGISTRATION_CHECK)
    @Operation(summary = "Register", responses = {
            @ApiResponse(responseCode = "201",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorResponse.class),
                                    examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_INPUT_ERROR))
                    }),
            @ApiResponse(responseCode = "409",
                    description = EMAIL_ALREADY_TAKEN,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_EMAIL_TAKEN_EXAMPLE)))
    })
    ResponseEntity<GenericResponse> registration(@Valid @RequestBody RegistrationRequest request,
                                                 BindingResult bindingResult);

    @PostMapping(REGISTRATION_CODE)
    @Operation(summary = "Send account verification email", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))),
            @ApiResponse(responseCode = "400",
                    description = "Validation error",
                    content = {
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ApiErrorResponse.class),
                                    examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_INPUT_ERROR))
                    }),
            @ApiResponse(responseCode = "404",
                    description = USER_NOT_FOUND,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_USER_NOT_FOUND))),
            @ApiResponse(responseCode = "409",
                    description = ACCOUNT_ALREADY_VERIFIED,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_ACCOUNT_ALREADY_VERIFIED)))
    })
    ResponseEntity<GenericResponse> sendRegistrationCode(@Valid @RequestBody ProcessEmailRequest request,
                                                         BindingResult bindingResult);


    @GetMapping(REGISTRATION_ACTIVATE_CODE)
    @Operation(summary = "Activate account", responses = {
            @ApiResponse(responseCode = "200",
                    description = "Successful response",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RegistrationEndResponse.class))),
            @ApiResponse(responseCode = "404",
                    description = ACTIVATION_CODE_NOT_FOUND,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_AC_NOT_FOUND))),
            @ApiResponse(responseCode = "409",
                    description = ACCOUNT_ALREADY_VERIFIED,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_ACCOUNT_ALREADY_VERIFIED)))
    })
    ResponseEntity<RegistrationEndResponse> checkRegistrationCode(@PathVariable String code);
}
