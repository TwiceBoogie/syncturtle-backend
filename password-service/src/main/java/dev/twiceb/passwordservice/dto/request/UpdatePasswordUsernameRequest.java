package dev.twiceb.passwordservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdatePasswordUsernameRequest {
    @NotNull
    @NotBlank
    private String username;
}
