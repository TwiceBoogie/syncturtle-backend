package dev.twiceb.userservice.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;

import static dev.twiceb.common.constants.ErrorMessage.EMAIL_NOT_VALID;

@Data
public class ProcessEmailRequest {

    @Email(regexp=".+@.+\\..+", message=EMAIL_NOT_VALID)
    private String email;
}
