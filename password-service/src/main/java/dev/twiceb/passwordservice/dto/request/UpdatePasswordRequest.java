package dev.twiceb.passwordservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Data
public class UpdatePasswordRequest {

    @NotNull
    private Long id;

    @NotNull
    private String username;

    @NotNull
    @Size(min = 8, message = SHORT_PASSWORD)
    private String password;

    @NotNull
    private String confirmPassword;
}
