package dev.twiceb.userservice.service.impl;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.userservice.model.PasswordResetOtp;
import dev.twiceb.userservice.model.PasswordResetToken;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.PasswordResetOtpRepository;
import dev.twiceb.userservice.repository.PasswordResetTokenRepository;
import dev.twiceb.userservice.repository.UserRepository;
import dev.twiceb.userservice.service.EmailService;
import dev.twiceb.userservice.service.PasswordResetService;
import dev.twiceb.userservice.service.UserService;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

import static dev.twiceb.common.constants.ErrorMessage.OTP_HAS_EXPIRED;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final EmailService emailService;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Map<String, String> createAndSendPasswordResetOtpEmail(User user, String otp, String hashedOtp) {
        PasswordResetOtp otpEntity = new PasswordResetOtp();
        otpEntity.setHashedOtp(hashedOtp);
        otpEntity.setUser(user);
        user.getPasswordResetOtps().add(otpEntity);
        user = userRepository.save(user);
        sendPasswordResetEmail(user, otp);
        return Map.of("message", "Check your email for the password otp");
    }

    @Override
    @Transactional
    public Map<String, String> verifyOtp(String hashedOtp, String token) {
        PasswordResetOtp otpEntity = validateAndExpirePasswordResetOtp(hashedOtp);
        User user = createPasswordResetTokenAndSaveUser(otpEntity.getUser(), token);
        sendPasswordResetLinkEmail(user, token);
        return Map.of("message", "OTP verified, check your email for the password reset link.");
    }

    @Override
    @Transactional
    public PasswordResetToken validateAndExpirePasswordResetToken(String token) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findPasswordResetTokenByToken(token);
        if (resetToken == null) {
            throw new ApiRequestException("Password reset token is invalid", HttpStatus.NOT_FOUND);
        }

        if (LocalDateTime.now().isAfter(resetToken.getExpirationTime())) {
            throw new ApiRequestException("Reset token has expired", HttpStatus.BAD_REQUEST);
        }
        resetToken.setExpirationTime(LocalDateTime.now());
        resetToken.setToken("");

        return passwordResetTokenRepository.save(resetToken);
    }

    private User createPasswordResetTokenAndSaveUser(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        user.getPasswordResetTokens().add(resetToken);
        return userRepository.save(user);
    }

    private void sendPasswordResetEmail(User user, String otp) {
        emailService.sendPasswordResetEmail(user, otp);
    }

    private PasswordResetOtp validateAndExpirePasswordResetOtp(String hashedOtp) {
        PasswordResetOtp otpEntity = passwordResetOtpRepository.findByHashedOtp(hashedOtp);
        if (otpEntity == null) {
            throw new ApiRequestException("No OTP found or match", HttpStatus.NOT_FOUND);
        }

        if (LocalDateTime.now().isAfter(otpEntity.getExpirationTime())) {
            throw new ApiRequestException(OTP_HAS_EXPIRED, HttpStatus.BAD_REQUEST);
        }

        otpEntity.setExpirationTime(LocalDateTime.now());
        otpEntity.setHashedOtp("");
        passwordResetOtpRepository.save(otpEntity);

        return otpEntity;
    }

    private void sendPasswordResetLinkEmail(User user, String token) {
        emailService.sendPasswordResetLinkEmail(user, token);
    }
}
