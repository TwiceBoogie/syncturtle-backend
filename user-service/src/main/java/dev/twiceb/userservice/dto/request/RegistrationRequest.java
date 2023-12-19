package dev.twiceb.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Data
public class RegistrationRequest {
    @Email(regexp=".+@.+\\..+",message=EMAIL_NOT_VALID)
    private String email;

    @NotBlank(message=NAME_NOT_VALID)
    private String firstName;

    @NotBlank(message=NAME_NOT_VALID)
    private String lastName;

    @NotBlank(message=PASSWORD_NOT_VALID)
    @Size(min=8,message=SHORT_PASSWORD)
    private String password;

    @NotBlank(message=PASSWORD_NOT_VALID)
    @Size(min=8,message=SHORT_PASSWORD)
    private String passwordConfirm;
}
