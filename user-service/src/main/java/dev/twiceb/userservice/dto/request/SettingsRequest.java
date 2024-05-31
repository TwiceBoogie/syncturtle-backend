package dev.twiceb.userservice.dto.request;

import dev.twiceb.common.dto.request.SettingsRequestGroup;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import static dev.twiceb.common.constants.ErrorMessage.INCORRECT_USERNAME_LENGTH;

@Data
@Schema(description = "Settings request model (used for multiple endpoints)")
public class SettingsRequest {

    @NotBlank(groups = SettingsRequestGroup.UsernameUpdateGroup.class, message = "Username cannot be blank")
    @Size(min = 8, max = 50, groups = SettingsRequestGroup.UsernameUpdateGroup.class, message = INCORRECT_USERNAME_LENGTH)
    @Schema(example = "RandomTestUser123")
    private String username;

    @NotBlank(groups = SettingsRequestGroup.EmailUpdateGroup.class, message = "Email cannot be blank")
    @Email(groups = SettingsRequestGroup.EmailUpdateGroup.class, message = "Input must be a valid email") // Corrected grammar
    @Schema(description = "User's email address", example = "HelloThere@test.com")
    private String email;

    @NotBlank(groups = SettingsRequestGroup.PhoneUpdateGroup.class, message = "Country code cannot be blank")
    @Pattern(groups = SettingsRequestGroup.PhoneUpdateGroup.class, regexp = "[A-Z]{2}", message = "Country code must be a 2-letter ISO code")
    @Schema(description = "ISO Country code", example = "US")
    private String countryCode;

    @Min(value = 100000000, message = "Phone number must be at least 9 digits long")
    @Max(value = 999999999999999L, message = "Phone number must be less than 16 digits")
    @Schema(description = "User's phone number", example = "1234567890")
    private Long phone;

    @Pattern(regexp = "male|female|other", message = "Gender must be 'male', 'female', or 'other'")
    @Schema(description = "User's gender", example = "male")
    private String gender;
}
