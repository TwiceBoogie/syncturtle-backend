package dev.twiceb.userservice.service;

import java.time.LocalDateTime;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.model.UserDevice;

public interface EmailService {
    void sendMagicCodeEmail(String email, String magicCode);

    void sendPasswordResetEmail(User user, String otp);

    void sendPasswordResetLinkEmail(User user, String token);

    void sendUsernameToUsersEmail(String username, String email);

    void sendDeviceVerificationEmail(User user, String verificationCode, String ipAddress,
            String userAgent);

    void sendPasswordChangeNotificationEmail(UserDevice device, String verificationCode,
            LocalDateTime expDateTime, String ipAddress);
}
