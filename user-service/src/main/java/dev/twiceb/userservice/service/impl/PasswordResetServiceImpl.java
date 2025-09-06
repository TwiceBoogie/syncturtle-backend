package dev.twiceb.userservice.service.impl;

import dev.twiceb.userservice.domain.model.PasswordResetToken;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.repository.PasswordResetOtpRepository;
import dev.twiceb.userservice.domain.repository.PasswordResetTokenRepository;
import dev.twiceb.userservice.domain.repository.UserRepository;
import dev.twiceb.userservice.service.EmailService;
import dev.twiceb.userservice.service.PasswordResetService;
import dev.twiceb.userservice.service.util.UserServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserServiceHelper helper;

    @Override
    public Map<String, String> createAndSendPasswordResetOtpEmail(User user) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'createAndSendPasswordResetOtpEmail'");
    }

    @Override
    public Map<String, String> verifyOtp(String otp) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'verifyOtp'");
    }

    @Override
    public Optional<PasswordResetToken> validateAndExpirePasswordResetToken(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException(
                "Unimplemented method 'validateAndExpirePasswordResetToken'");
    }

}
