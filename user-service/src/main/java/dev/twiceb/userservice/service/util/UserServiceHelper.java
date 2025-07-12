package dev.twiceb.userservice.service.util;

import dev.twiceb.common.enums.AuthErrorCodes;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.exception.AuthException;
import dev.twiceb.common.exception.InputFieldException;
import dev.twiceb.common.mapper.FieldErrorMapper;
import dev.twiceb.common.mapper.FieldErrorMapper.ValidationContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Optional;
import java.util.Random;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceHelper {

    private static final int RANDOM_STRING_LENGTH = 6;
    private static final int RANDOM_STRING_LENGTH_CODE = 16;
    private static final int OTP_LENGTH = 6;

    private final PasswordEncoder passwordEncoder;

    public void processBindingResults(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InputFieldException(bindingResult);
        }
    }

    public void processInputErrors(BindingResult result, ValidationContext ctx) {
        if (result.hasErrors()) {
            throw FieldErrorMapper.mapToAuthException(result, ctx);
        }
    }

    public void processPassword(String password1, String password2) {
        if (!password1.equals(password2)) {
            throw new AuthException(AuthErrorCodes.INVALID_PASSWORD_CONFIRM);
        }
    }

    public String generateRandomCode() {
        byte[] randomBytes = new byte[RANDOM_STRING_LENGTH_CODE];
        return generateRandomString(randomBytes);
    }

    public String generateRandomSuffix() {
        byte[] randomBytes = new byte[RANDOM_STRING_LENGTH];
        return generateRandomString(randomBytes);
    }

    public String generateOTP() {
        String numbers = "0123456789";
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < OTP_LENGTH; i++) {
            int index = random.nextInt(numbers.length());
            otp.append(numbers.charAt(index));
        }

        return otp.toString();
    }

    public String hash(String value) {
        try {
            return hashCodeString(value.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String hash(byte[] value) {
        try {
            return hashCodeString(value);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generateRandomString(byte[] randomBytes) {
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String hashCodeString(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(data);

        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte hash : encodedHash) {
            String hex = Integer.toHexString(0xff & hash);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public boolean isPasswordsEqual(String password, String passwordFromDb) {
        return passwordEncoder.matches(password, passwordFromDb);
    }

    public String decodeAndHashDeviceVerificationCode(String deviceVerificationCode) {
        return decodeBase64(deviceVerificationCode).map(this::hash).orElseThrow(() -> {
            log.error("Invalid device verification code: {}", deviceVerificationCode);
            return new ApiRequestException("Device verification invalid", HttpStatus.BAD_REQUEST);
        });
    }

    private Optional<byte[]> decodeBase64(String input) {
        try {
            return Optional.of(Base64.getUrlDecoder().decode(input));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    // @Override
    // protected EntityManager getEntityManager() {
    // return entityManager;
    // }
}
