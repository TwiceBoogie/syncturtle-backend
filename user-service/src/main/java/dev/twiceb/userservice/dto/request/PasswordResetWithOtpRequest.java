package dev.twiceb.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static dev.twiceb.common.constants.ErrorMessage.EMPTY_PASSWORD;
import static dev.twiceb.common.constants.ErrorMessage.SHORT_PASSWORD;

@Data
@EqualsAndHashCode(callSuper = false)
public class PasswordResetWithOtpRequest extends PasswordOtpRequest {

    @NotBlank(message = EMPTY_PASSWORD)
    @Size(min = 8, message = SHORT_PASSWORD)
    private String password;
    // TODO: make own validation constraint so BindingResult captures it.
    @NotBlank(message = EMPTY_PASSWORD)
    @Size(min = 8, message = SHORT_PASSWORD)
    private String confirmPassword;
}
