package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.userservice.amqp.AmqpPublisher;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.model.UserDevice;
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

        private final AmqpPublisher amqpPublisher;

        @Override
        public void sendMagicCodeEmail(String email, String magicCode) {
                System.out.println("should be in here");
                EmailRequest emailRequest =
                                new EmailRequest.Builder(email, "Magic Code", "magicCode-template")
                                                .attributes(Map.of("magicCode", magicCode)).build();
                amqpPublisher.sendEmail(emailRequest);
        }

        @Override
        public void sendPasswordResetEmail(User user, String otp) {
                EmailRequest emailRequest = new EmailRequest.Builder(user.getEmail(),
                                "Password Reset OTP", "forgotPassword-template")
                                                .attributes(Map.of("fullName",
                                                                user.getFirstName() + " " + user
                                                                                .getLastName(),
                                                                "passwordResetOtp",
                                                                otp.toCharArray()))
                                                .build();

                amqpPublisher.sendEmail(emailRequest);
        }

        @Override
        public void sendPasswordResetLinkEmail(User user, String token) {
                EmailRequest emailRequest = new EmailRequest.Builder(user.getEmail(),
                                "Password Reset Token", "forgotPassword-template")
                                                .attributes(Map.of("fullName",
                                                                user.getFirstName() + " " + user
                                                                                .getLastName(),
                                                                "passwordResetToken", token))
                                                .build();

                amqpPublisher.sendEmail(emailRequest);
        }

        @Override
        public void sendUsernameToUsersEmail(String username, String email) {
                EmailRequest emailRequest = new EmailRequest.Builder(email, "Username",
                                "forgotUsername-template").attributes(Map.of("username", username))
                                                .build();

                amqpPublisher.sendEmail(emailRequest);
        }

        @Override
        public void sendDeviceVerificationEmail(User user, String verificationCode,
                        String ipAddress, String userAgent) {
                String formattedDateTime = formatDateTime(LocalDateTime.now());
                EmailRequest emailRequest = new EmailRequest.Builder(user.getEmail(),
                                "Device Verification", "deviceVerification-template")
                                                .attributes(Map.of("fullName",
                                                                user.getFirstName() + " " + user
                                                                                .getLastName(),
                                                                "deviceVerificationCode",
                                                                verificationCode, "userIp",
                                                                ipAddress, "userAgent", userAgent,
                                                                "accessDate", formattedDateTime))
                                                .build();
                amqpPublisher.sendEmail(emailRequest);
        }

        private String formatDateTime(LocalDateTime dateTime) {
                DateTimeFormatter formatter = DateTimeFormatter
                                .ofPattern("EEEE, MMMM d 'at' hh:mma", Locale.ENGLISH);
                return dateTime.format(formatter);
        }

        @Override
        public void sendPasswordChangeNotificationEmail(UserDevice device, String verificationCode,
                        LocalDateTime expDateTime, String ipAddress) {
                String formattedDateTime = formatDateTime(LocalDateTime.now());
                User user = device.getUser();
                EmailRequest emailRequest = new EmailRequest.Builder(user.getEmail(),
                                "Password Change", "passwordChange-template")
                                                .attributes(Map.of("fullName",
                                                                user.getFirstName() + " " + user
                                                                                .getLastName(),
                                                                "lockAccountCode", verificationCode,
                                                                "userIp", ipAddress, "accessDate",
                                                                formattedDateTime))
                                                .build();
                amqpPublisher.sendEmail(emailRequest);
                throw new UnsupportedOperationException(
                                "Unimplemented method 'sendPasswordChangeNotificationEmail'");
        }
}
