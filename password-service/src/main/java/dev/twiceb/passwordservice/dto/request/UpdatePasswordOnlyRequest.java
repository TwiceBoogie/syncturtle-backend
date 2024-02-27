package dev.twiceb.passwordservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Data
public class UpdatePasswordOnlyRequest {
    @NotNull
    @Size(min = 8, message = SHORT_PASSWORD)
    private String password;

    @NotNull
    private String confirmPassword;
}
