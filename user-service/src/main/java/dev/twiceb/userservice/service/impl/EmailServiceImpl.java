package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.userservice.amqp.MessagePublisher;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.model.UserDevice;
import dev.twiceb.userservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
        private final MessagePublisher amqpPublisher;

        @Override
        public void sendMagicCodeEmail(String email, String magicCode) {
                String subject = String.format("Your unique SyncTurtle login code is %s", magicCode);
                EmailRequest emailRequest = new EmailRequest.Builder(email, subject, "plane-template")
                                .attributes(Map.of("title", subject, "magicCode", magicCode, "email", email))
                                .build();
                amqpPublisher.sendEmail(emailRequest);
        }

        @Override
        public void sendPasswordResetEmail(User user, String otp) {
                String subject = String.format("Reset your SyncTurtle password with OTP %s", otp);
                String fullName = String.format("%s %s", user.getFirstName(), user.getLastName());
                EmailRequest emailRequest = new EmailRequest.Builder(user.getEmail(), subject,
                                "forgotPassword-template")
                                .attributes(
                                                Map.of("fullName", fullName, "passwordResetOtp", otp.toCharArray()))
                                .build();
                amqpPublisher.sendEmail(emailRequest);
        }

        @Override
        public void sendPasswordResetLinkEmail(User user, String token) {
                String subject = "Reset your SyncTurtle password";
                String fullName = String.format("%s %s", user.getFirstName(), user.getLastName());
                EmailRequest emailRequest = new EmailRequest.Builder(user.getEmail(), subject,
                                "forgotPassword-template")
                                .attributes(Map.of("fullname", fullName, "passwordResetToken", token))
                                .build();
                amqpPublisher.sendEmail(emailRequest);
        }

        @Override
        public void sendUsernameToUsersEmail(String username, String email) {
                // String subject = String.format("Here's your SyncTurtle username: %s",
                // username);
                throw new UnsupportedOperationException("Unimplemented method 'sendUsernameToUsersEmail'");
        }

        @Override
        public void sendDeviceVerificationEmail(User user, String magicCode, String ipAddress,
                        String userAgent) {
                String subject = String.format("Verify new login to SyncTurtle - code %s", magicCode);
                String formattedDateTime = formatDateTime(LocalDateTime.now());
                EmailRequest emailRequest = new EmailRequest.Builder(user.getEmail(), subject,
                                "deviceVerification-template")
                                .attributes(Map.of("magicCode", magicCode, "userIp", ipAddress, "userAgent",
                                                userAgent, "accessDate", formattedDateTime, "email",
                                                user.getEmail()))
                                .build();
                amqpPublisher.sendEmail(emailRequest);
        }

        @Override
        public void sendPasswordChangeNotificationEmail(UserDevice device, String verificationCode,
                        LocalDateTime expDateTime, String ipAddress) {
                String subject = "Your SyncTurtle passwod was changed";
                User user = device.getUser();
                String fullName = String.format("%s %s", user.getFirstName(), user.getLastName());
                String formattedDateTime = formatDateTime(LocalDateTime.now());
                EmailRequest emailRequest = new EmailRequest.Builder(user.getEmail(), subject,
                                "passwordChange-template")
                                .attributes(
                                                Map.of("fullName", fullName, "lockAccountCode", verificationCode,
                                                                "userIp", ipAddress, "accessDate", formattedDateTime))
                                .build();
                amqpPublisher.sendEmail(emailRequest);
        }

        private String formatDateTime(LocalDateTime dateTime) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d 'at' hh:mma", Locale.ENGLISH);
                return dateTime.format(formatter);
        }
}
