package dev.twiceb.passwordservice.dto.request;

import static dev.twiceb.common.constants.ErrorMessage.EMPTY_DOMAIN;
import static dev.twiceb.common.constants.ErrorMessage.EMPTY_PASSWORD;
import static dev.twiceb.common.constants.ErrorMessage.EMPTY_PASSWORD_CONFIRMATION;
import static dev.twiceb.common.constants.ErrorMessage.SHORT_PASSWORD;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePasswordRequest {
    @NotBlank(message = EMPTY_DOMAIN)
    private String domain;

    @NotBlank(message = EMPTY_PASSWORD)
    @Size(min = 8, message = SHORT_PASSWORD)
    private String password;

    @NotBlank(message = EMPTY_PASSWORD_CONFIRMATION)
    private String confirmPassword;
}
