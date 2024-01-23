package dev.twiceb.userservice.service.util;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.util.ServiceHelper;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.model.ActivationCode;
import dev.twiceb.userservice.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Component
@RequiredArgsConstructor
public class UserServiceHelper extends ServiceHelper {

    @PersistenceContext
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    public void isPasswordSame(String password1, String password2) {
        if (!password1.equals(password2)) {
            throw new ApiRequestException(PASSWORDS_NOT_MATCH, HttpStatus.BAD_REQUEST);
        }
    }

    public UserServiceHelper processBindingResults(BindingResult bindingResult) {
        this.processInputErrors(bindingResult);
        return this;
    }

    public void processPassword(String password1, String password2) {
        if (!password1.equals(password2)) {
            throw new ApiRequestException(PASSWORDS_NOT_MATCH, HttpStatus.BAD_REQUEST);
        }

        if (password1.length() < 9) {
            throw new ApiRequestException(PASSWORD_LENGTH_ERROR, HttpStatus.BAD_REQUEST);
        }
    }

    public User createUserEntity(RegistrationRequest request) {
        return new User(
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                encodePassword(request.getPassword())
        );
    }

    public ActivationCode createActivationCodeEntity(User user) {
        return new ActivationCode(
                generateActivationCode(),
                LocalDateTime.now().plusHours(24),
                user
        );
    }

    public String regenerateActivationCode() {
        return generateActivationCode();
    }

    private void isActivationCodeExpired(LocalDateTime expirationTime) {
        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isAfter(expirationTime))
            throw new ApiRequestException(ACTIVATION_CODE_EXPIRED, HttpStatus.GONE);
    }

    public User processUser(User user, LocalDateTime expirationTime) {
        isActivationCodeExpired(expirationTime);

        if (user.isVerified()) {
            throw new ApiRequestException(ACCOUNT_ALREADY_VERIFIED, HttpStatus.CONFLICT);
        }

        user.setUserStatus("Active");
        user.setVerified(true);
        return user;
    }

    private String generateActivationCode() {
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] randomBytes = new byte[16];

            secureRandom.nextBytes(randomBytes);
            String base64Encoded = Base64.getEncoder().encodeToString(randomBytes);

            return URLEncoder.encode(base64Encoded, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new ApiRequestException(ACTIVATION_CODE_GENERATION_FAIL, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
