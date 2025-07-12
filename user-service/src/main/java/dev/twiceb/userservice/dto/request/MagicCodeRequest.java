package dev.twiceb.userservice.dto.request;

import static dev.twiceb.common.constants.ErrorMessage.MAGIC_CODE_INVALID;
import static dev.twiceb.common.constants.ErrorMessage.MAGIC_CODE_REGEX;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class MagicCodeRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = MAGIC_CODE_REGEX, message = MAGIC_CODE_INVALID)
    private String magicCode;
}
