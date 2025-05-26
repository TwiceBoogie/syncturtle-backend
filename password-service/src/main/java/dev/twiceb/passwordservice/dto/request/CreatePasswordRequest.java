package dev.twiceb.passwordservice.dto.request;

import static dev.twiceb.common.constants.ErrorMessage.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.*;

@Data
public class CreatePasswordRequest {
    private UUID encryptionId;

    @NotBlank(message = EMPTY_DOMAIN)
    private String domain;

    @NotBlank(message = EMPTY_WEBSITE_URL)
    private String websiteUrl;

    @NotBlank(message = EMPTY_USERNAME)
    private String username;

    @NotBlank(message = EMPTY_PASSWORD)
    @Size(min = 8, message = SHORT_PASSWORD)
    private String password;

    @Pattern(regexp = "^[^<>&]*$", message = "HTML tags are not allowed in notes")
    private String notes;

    @NotBlank(message = EMPTY_PASSWORD_CONFIRMATION)
    private String confirmPassword;

    @NotNull
    private Long passwordExpiryPolicy;

    @NotNull
    private Set<Long> category = new HashSet<>();
}
