package dev.twiceb.passwordservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdatePasswordNoteRequest {
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[^<>&]*$", message = "HTML tags are not allowed in notes")
    private String notes;
}
