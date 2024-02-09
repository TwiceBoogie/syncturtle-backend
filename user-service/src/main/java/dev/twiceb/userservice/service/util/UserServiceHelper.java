package dev.twiceb.userservice.service.util;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.util.EnvelopeEncryption;
import dev.twiceb.common.util.ServiceHelper;
import dev.twiceb.userservice.dto.request.RegistrationRequest;
import dev.twiceb.userservice.enums.ActivationCodeType;
import dev.twiceb.userservice.model.ActivationCode;
import dev.twiceb.userservice.model.LoginAttemptPolicy;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.model.UserDevice;
import dev.twiceb.userservice.repository.LoginAttemptPolicyRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.vault.core.VaultTemplate;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Plaintext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
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
    private final VaultTemplate vaultTemplate;
    private final LoginAttemptPolicyRepository loginAttemptPolicyRepository;

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
        LoginAttemptPolicy policy = loginAttemptPolicyRepository.findById(1L).orElseThrow(
                () -> new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR)
        );
        return new User(
                request.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                encodePassword(request.getPassword()),
                policy
        );
    }

    public ActivationCode createActivationCodeEntity(User user, ActivationCodeType codeType) {
        return new ActivationCode(
                generateActivationCode(),
                codeType,
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

    public String encryptDeviceKey(String deviceToken) {
        Plaintext plaintext = Plaintext.of(deviceToken);
        return vaultTemplate.opsForTransit().encrypt("deviceKey", plaintext).toString();
    }

    public String decryptDeviceKey(String deviceToken) {
        Ciphertext ciphertext = Ciphertext.of(deviceToken);
        return vaultTemplate.opsForTransit().decrypt("deviceKey", ciphertext).toString();
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
