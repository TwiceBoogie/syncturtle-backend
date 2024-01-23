package dev.twiceb.passwordservice.dto.request;

import static dev.twiceb.common.constants.ErrorMessage.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePasswordRequest {
    @NotBlank(message = EMPTY_DOMAIN)
    private String domain;

    @NotBlank(message = EMPTY_USERNAME)
    private String username;

    @NotBlank(message = EMPTY_PASSWORD)
    @Size(min = 8, message = SHORT_PASSWORD)
    private String password;

    @Pattern(regexp = "^[^<>&]*$", message = "HTML tags are not allowed in notes")
    private String notes;

    @NotBlank(message = EMPTY_PASSWORD_CONFIRMATION)
    private String confirmPassword;

    @NotBlank
    private String passwordExpiryPolicy;
}
