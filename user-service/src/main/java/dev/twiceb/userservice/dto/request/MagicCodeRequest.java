package dev.twiceb.userservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MagicCodeRequest {

    @Email
    @NotNull
    private String email;
    private String magicCode;
}
