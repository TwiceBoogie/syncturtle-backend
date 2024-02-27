package dev.twiceb.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordOtpRequest {

    @NotBlank(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 characters long")
    @Pattern(regexp = "\\d+", message = "OTP must contain only digits")
    private String otp;
}
