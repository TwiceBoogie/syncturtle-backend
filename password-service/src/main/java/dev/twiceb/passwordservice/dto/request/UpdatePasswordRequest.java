package dev.twiceb.passwordservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Data
public class UpdatePasswordRequest {
    @NotNull
    @Pattern(regexp = "^\\d+$", message = INVALID_ID_PROVIDED)
    private Long id;

    @NotBlank(message = EMPTY_USERNAME)
    private String username;

    @NotBlank(message = EMPTY_PASSWORD)
    @Size(min = 8, message = SHORT_PASSWORD)
    private String password;

    @NotBlank(message = EMPTY_PASSWORD_CONFIRMATION)
    private String confirmPassword;
}
