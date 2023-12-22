package dev.twiceb.passwordsservice.dto.request;

import static dev.twiceb.common.constants.ErrorMessage.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePasswordRequest {
    @NotBlank(message = EMPTY_DOMAIN)
    private String domain;

    @NotBlank(message = EMPTY_PASSWORD)
    @Size(min = 8, message = SHORT_PASSWORD)
    private String password;

    @NotBlank(message = EMPTY_PASSWORD_CONFIRMATION)
    private String confirmPassword;
}
