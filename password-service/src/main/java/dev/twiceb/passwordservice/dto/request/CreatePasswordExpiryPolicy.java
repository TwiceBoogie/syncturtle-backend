package dev.twiceb.passwordservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreatePasswordExpiryPolicy {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String policyName;

    @Min(value = 30)
    @NotBlank
    private int maxExpiryDays;

    @Min(value = 1)
    @NotBlank
    private int notificationDays;
}
