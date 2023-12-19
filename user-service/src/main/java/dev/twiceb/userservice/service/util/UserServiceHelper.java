package dev.twiceb.userservice.service.util;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.util.ServiceHelper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

import static dev.twiceb.common.constants.ErrorMessage.ACTIVATION_CODE_GENERATION_FAIL;
import static dev.twiceb.common.constants.ErrorMessage.PASSWORDS_NOT_MATCH;

@Component
public class UserServiceHelper extends ServiceHelper {

    public void isPasswordSame(String password1, String password2) {
        if (!password1.equals(password2)) {
            throw new ApiRequestException(PASSWORDS_NOT_MATCH, HttpStatus.BAD_REQUEST);
        }
    }

    public String generateActivationCode() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] randomBytes = new byte[16];

            secureRandom.nextBytes(randomBytes);
            return Base64.getEncoder().encodeToString(randomBytes);
        } catch (Exception e) {
            throw new ApiRequestException(ACTIVATION_CODE_GENERATION_FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
