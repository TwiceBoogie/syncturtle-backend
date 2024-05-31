package dev.twiceb.apigateway.service.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import dev.twiceb.common.exception.ApiRequestException;
import lombok.extern.slf4j.Slf4j;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Slf4j
@Component
public class UserServiceHelper {

    public String decodeAndHashDeviceVerificationCode(String deviceVerificationCode) {
        return decodeBase64(deviceVerificationCode)
                .map(this::hash)
                .orElseThrow(() -> {
                    log.error("Invalid device verification code: {}", deviceVerificationCode);
                    return new ApiRequestException("Device verification invalid", HttpStatus.BAD_REQUEST);
                });
    }

    private String hash(byte[] value) {
        try {
            return hashCodeString(value);
        } catch (NoSuchAlgorithmException e) {
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

    private Optional<byte[]> decodeBase64(String input) {
        try {
            return Optional.of(Base64.getUrlDecoder().decode(input));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
