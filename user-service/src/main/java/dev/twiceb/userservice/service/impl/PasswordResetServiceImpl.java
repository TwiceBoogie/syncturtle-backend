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
import dev.twiceb.userservice.service.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static dev.twiceb.common.constants.ErrorMessage.OTP_HAS_EXPIRED;
import static dev.twiceb.common.constants.ErrorMessage.OTP_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserServiceHelper helper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Map<String, String> createAndSendPasswordResetOtpEmail(User user) {
        PasswordResetOtp otpEntity = new PasswordResetOtp();
        String otp = helper.generateOTP();
        otpEntity.setHashedOtp(helper.decodeAndHashDeviceVerificationCode(otp));
        otpEntity.setUser(user);
        user.getPasswordResetOtps().add(otpEntity);
        user = userRepository.save(user);
        sendPasswordResetEmail(user, otp);
        return Map.of("message", "Check your email for the password otp");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Map<String, String> verifyOtp(String otp) {
        PasswordResetOtp otpEntity = validateAndExpirePasswordResetOtp(helper.hash(otp));
        String resetToken = helper.generateRandomCode();
        User user = createPasswordResetTokenAndSaveUser(otpEntity.getUser(),
                helper.decodeAndHashDeviceVerificationCode(resetToken));
        sendPasswordResetLinkEmail(user, resetToken);
        return Map.of("message", "OTP verified, check your email for the password reset link.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public Optional<PasswordResetToken> validateAndExpirePasswordResetToken(String token) {
        String hashedToken = helper.decodeAndHashDeviceVerificationCode(token);
        return Optional.ofNullable(passwordResetTokenRepository.findPasswordResetTokenByToken(hashedToken))
                .map(tokenEntity -> {
                    if (LocalDateTime.now().isAfter(tokenEntity.getExpirationTime())) {
                        throw new ApiRequestException("Reset token has expired", HttpStatus.BAD_REQUEST);
                    }
                    return invalidatePasswordResetToken(tokenEntity);
                });
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
        return Optional.ofNullable(passwordResetOtpRepository.findByHashedOtp(hashedOtp))
                .map(otpEntity -> {
                    if (LocalDateTime.now().isAfter(otpEntity.getExpirationTime())) {
                        throw new ApiRequestException(OTP_HAS_EXPIRED, HttpStatus.BAD_REQUEST);
                    }
                    return invalidateOtp(otpEntity);
                })
                .orElseThrow(() -> new ApiRequestException(OTP_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private PasswordResetOtp invalidateOtp(PasswordResetOtp otpEntity) {
        otpEntity.setExpirationTime(LocalDateTime.now());
        otpEntity.setHashedOtp("");
        return passwordResetOtpRepository.save(otpEntity);
    }

    private PasswordResetToken invalidatePasswordResetToken(PasswordResetToken tokenEntity) {
        tokenEntity.setExpirationTime(LocalDateTime.now());
        tokenEntity.setToken("");
        return passwordResetTokenRepository.save(tokenEntity);
    }

    private void sendPasswordResetLinkEmail(User user, String token) {
        emailService.sendPasswordResetLinkEmail(user, token);
    }
}
