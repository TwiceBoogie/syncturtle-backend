package dev.twiceb.userservice.service;

import dev.twiceb.userservice.model.PasswordResetToken;
import dev.twiceb.userservice.model.User;

import java.util.Map;

public interface PasswordResetService {
    Map<String, String> createAndSendPasswordResetOtpEmail(User user, String otp, String hashedOtp);
    Map<String, String> verifyOtp(String hashedOtp, String token);
    PasswordResetToken validateAndExpirePasswordResetToken(String token);
}
