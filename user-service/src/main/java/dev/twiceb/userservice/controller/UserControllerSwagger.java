package dev.twiceb.userservice.controller;

import dev.twiceb.common.dto.response.ApiErrorResponse;
import dev.twiceb.common.util.ApiResponseExamples;
import dev.twiceb.userservice.dto.response.AuthenticationResponse;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import static dev.twiceb.common.constants.ErrorMessage.JWT_TOKEN_EXPIRED;
import static dev.twiceb.common.constants.PathConstants.TOKEN;

@SecurityScheme(
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        name = "Authorization",
        in = SecuritySchemeIn.HEADER
)
@SecurityRequirement(name = "Authorization")
@ApiResponse(responseCode = "401",
        description = JWT_TOKEN_EXPIRED,
        content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = ApiErrorResponse.class),
                examples = @ExampleObject(value = ApiResponseExamples.API_ERROR_JWT)))
public interface UserControllerSwagger {

    @GetMapping(TOKEN)
    @ApiResponse(responseCode = "200",
            description = "Successful request",
            content = {
                    @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))
            })
    ResponseEntity<AuthenticationResponse> getUserByToken();
}
