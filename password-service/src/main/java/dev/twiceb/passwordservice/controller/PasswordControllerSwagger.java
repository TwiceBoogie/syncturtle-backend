package dev.twiceb.passwordservice.controller;

import dev.twiceb.common.dto.response.ApiErrorResponse;
import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.util.ApiResponseExamples;
import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.dto.request.SearchQueryRequest;
import dev.twiceb.passwordservice.dto.request.UpdatePasswordRequest;
import dev.twiceb.passwordservice.dto.response.EncryptionKeysResponse;
import dev.twiceb.passwordservice.dto.response.PasswordsResponse;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static dev.twiceb.common.constants.ErrorMessage.*;
import static dev.twiceb.common.constants.PathConstants.*;

@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        name = "Authorization",
        in = SecuritySchemeIn.HEADER
)
@SecurityScheme(
        name = "deviceAuth", // Reference name for device authentication
        type = SecuritySchemeType.APIKEY,
        in = SecuritySchemeIn.HEADER,
        paramName = "X-User-DeviceKey"  // Header name for device JWT
)
@SecurityRequirement(name = "Authorization")
@SecurityRequirement(name = "deviceAuth")
@ApiResponse(responseCode = "401",
        description = JWT_TOKEN_EXPIRED,
        content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_JWT)))
public interface PasswordControllerSwagger {

    @PostMapping
    @ApiResponse(responseCode = "201",
            description = "Successful request",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = GenericResponse.class))

    )
    @ApiResponse(responseCode = "409",
            description = DOMAIN_ALREADY_EXIST,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiErrorResponse.class),
                    examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_DOMAIN_EXIST))

    )
    ResponseEntity<GenericResponse> createNewPassword(@Valid @RequestBody CreatePasswordRequest request,
                                                      BindingResult bindingResult);

    @PatchMapping(UPDATE_PASSWORD)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))
            }
    )
    @ApiResponse(responseCode = "404",
            description = NO_RESOURCE_FOUND,
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_NO_RESOURCE)
                    )
            }
    )
    ResponseEntity<GenericResponse> updatePasswordOnly(@PathVariable("passwordId") UUID passwordId,
                                                       @Valid @RequestBody UpdatePasswordRequest request,
                                                       BindingResult bindingResult
    );

    @PatchMapping(UPDATE_PASSWORD_USERNAME)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))
            }
    )
    ResponseEntity<GenericResponse> updateUsername(@PathVariable("passwordId") UUID passwordId,
                                                   @Valid @RequestBody UpdatePasswordRequest request,
                                                   BindingResult bindingResult
    );

    @PatchMapping(UPDATE_PASSWORD_NOTES)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class)
                    )
            }
    )
    ResponseEntity<GenericResponse> updatePasswordNotes(@PathVariable("passwordId") UUID passwordId,
                                                        @Valid @RequestBody UpdatePasswordRequest request,
                                                        BindingResult bindingResult
    );

    @PutMapping("/tags/{passwordId}")
    @ApiResponse(responseCode = "204",
            description = "Successful request"
    )
    @ApiResponse(responseCode = "400",
            description = "Invalid tags",
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            }
    )
    @ApiResponse(responseCode = "404",
            description = NO_RESOURCE_FOUND,
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class))
            }
    )
    ResponseEntity<Void> updateTagsOnPassword(@Valid @RequestBody UpdatePasswordRequest request,
                                              @PathVariable("passwordId") UUID passwordId,
                                              BindingResult bindingResult
    );

    @PutMapping(FAVORITE_PASSWORD)
    @ApiResponse(responseCode = "204",
            description = "Successful request"
    )
    @ApiResponse(responseCode = "404",
            description = NO_RESOURCE_FOUND,
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_NO_RESOURCE))
            }
    )
    ResponseEntity<Void> favoritePassword(@Valid @RequestBody UpdatePasswordRequest request,
                                          @PathVariable("passwordId") UUID passwordId,
                                          BindingResult bindingResult
    );

    @GetMapping
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = GenericResponse.class)))
            }
    )
    ResponseEntity<List<PasswordsResponse>> getPasswords(@PageableDefault(size = 10) Pageable Pageable);

    @GetMapping(GET_PASSWORD_INFO)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PasswordsResponse.class))
            }
    )
    @ApiResponse(responseCode = "404",
            description = NO_RESOURCE_FOUND,
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_NO_RESOURCE))
            }
    )
    ResponseEntity<PasswordsResponse> getPasswordInfo(@PathVariable("keychainId") UUID keychainId);

    @GetMapping(GET_PASSWORD_WITH_CRITERIA)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PasswordsResponse.class)))
            }
    )
    ResponseEntity<List<PasswordsResponse>> getPasswordsByCriteria(@PathVariable("criteria") String criteria,
                                                                   @PageableDefault(size = 10) Pageable Pageable
    );

    @GetMapping(GET_DECRYPTED_PASSWORD)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))
            }
    )
    @ApiResponse(responseCode = "404",
            description = NO_RESOURCE_FOUND,
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_NO_RESOURCE))
            }
    )
    ResponseEntity<GenericResponse> getDecryptedPassword(@PathVariable("passwordId") UUID passwordId);

    @DeleteMapping(DELETE_PASSWORD)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))
            }
    )
    @ApiResponse(responseCode = "404",
            description = NO_RESOURCE_FOUND,
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_NO_RESOURCE))
            }
    )
    ResponseEntity<GenericResponse> deletePassword(@PathVariable("passwordId") UUID passwordId);

    @DeleteMapping(DELETE_ALL)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))
            }
    )
    ResponseEntity<GenericResponse> deleteAllPasswords();

    @GetMapping(GENERATE_RANDOM_PASSWORD)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = GenericResponse.class))
            }
    )
    @ApiResponse(responseCode = "400",
            description = NO_RESOURCE_FOUND,
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorResponse.class),
                            examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_GEN_PASSWORD_LENGTH))
            }
    )
    ResponseEntity<GenericResponse> generateRandomPassword(@PathVariable("length") int length);

    @PostMapping(SEARCH_BY_QUERY)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PasswordsResponse.class)))
            }
    )
    ResponseEntity<List<PasswordsResponse>> searchPasswordsByQuery(@Valid @RequestBody SearchQueryRequest request,
                                                                   BindingResult bindingResult,
                                                                   @PageableDefault(size = 10) Pageable Pageable
    );

    @GetMapping(GET_ENCRYPTION_KEYS)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EncryptionKeysResponse.class)))
            }
    )
    ResponseEntity<List<EncryptionKeysResponse>> getEncryptionKeys(@PageableDefault(size = 10) Pageable pageable);
}
