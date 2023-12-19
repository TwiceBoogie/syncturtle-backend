package dev.twiceb.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Data
public class EndRegistrationRequest {
    @Email(regexp = ".+@.+\\..+", message=EMAIL_NOT_VALID)
    private String email;

    @NotBlank(message=EMPTY_PASSWORD_CONFIRMATION)
    @Size(min=8, message=SHORT_PASSWORD)
    private String password;
}
