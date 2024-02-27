package dev.twiceb.userservice.service;

import dev.twiceb.userservice.model.User;

public interface EmailService {
    void sendPasswordResetEmail(User user, String otp);
    void sendPasswordResetLinkEmail(User user, String token);
    void sendUsernameToUsersEmail(String username, String email);
    void sendDeviceVerificationEmail(User user, String verificationCode, String ipAddress, String userAgent);
}
