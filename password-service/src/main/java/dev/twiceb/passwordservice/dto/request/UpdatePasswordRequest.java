package dev.twiceb.passwordservice.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UpdatePasswordRequest {
    private String username;
    @Pattern(regexp = "^[^<>&]*$", message = "HTML tags are not allowed in notes")
    private String notes;
    private String password;
    private String confirmPassword;
    private boolean isFavorite;
    private Long rotationPolicyId;
    @Size(max = 10, message = "Maximum 10 tags allowed")
    private Set<Long> tags = new HashSet<>();
}
