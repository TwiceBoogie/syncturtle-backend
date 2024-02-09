package dev.twiceb.passwordservice.service.util;

import dev.twiceb.common.util.EnvelopeEncryption;
import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.model.*;
import dev.twiceb.passwordservice.repository.*;
import dev.twiceb.passwordservice.repository.projection.DecryptedPasswordProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.util.ServiceHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Component
@RequiredArgsConstructor
public class PasswordHelperService extends ServiceHelper {

    @PersistenceContext
    private final EntityManager entityManager;
    private final EnvelopeEncryption envelopeEncryption;
    private final PasswordReuseStatisticRepository passwordReuseStatisticRepository;
    private final PasswordExpiryPolicyRepository passwordExpiryPolicyRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(PasswordHelperService.class);

    public PasswordHelperService processBindingResults(BindingResult bindingResult) {
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

//    public PasswordExpiryConfig createUserPasswordExpirySetting(String policyName) {
//        PasswordExpiryPolicy policy = passwordExpiryPolicyRepository.findByPolicyName(policyName);
//        if (policy == null) {
//            throw new ApiRequestException(PASSWORD_EXPIRY_POLICY_NOT_FOUND, HttpStatus.BAD_GATEWAY);
//        }
//        PasswordExpiryConfig expirySetting = new PasswordExpiryConfig();
//        expirySetting.setPasswordExpiryPolicy(policy);
//
//        return expirySetting;
//    }

    @Transactional
    public void buildAnalyticEntities(Keychain keychain, String plainTextPassword) {
        keychain.setComplexityMetric(createPasswordComplexity(plainTextPassword));
        keychain.getComplexityMetric().setKeychain(keychain);

        String passwordHashed = passwordEncoder.encode(plainTextPassword);
        PasswordReuseStatistic passwordReuseStatistic = isPasswordReuseStaticExistByPasswordHash(
                keychain.getAccount().getId(), passwordHashed
        );
        if (passwordReuseStatistic == null) {
            passwordReuseStatistic = new PasswordReuseStatistic(keychain.getAccount(), passwordHashed);
        }
        passwordReuseStatisticRepository.save(passwordReuseStatistic);
    }

    private PasswordComplexityMetric createPasswordComplexity(String plainTextPassword) {
        return PasswordComplexityAnalyzer.analyzePassword(plainTextPassword);
    }

    private PasswordReuseStatistic isPasswordReuseStaticExistByPasswordHash(Long accountId, String passwordHashed) {
        List<PasswordReuseStatistic> reuseStatisticList = passwordReuseStatisticRepository.findAllByAccountId(accountId);

        for (PasswordReuseStatistic entity : reuseStatisticList) {
            if (passwordEncoder.matches(passwordHashed, entity.getPasswordHash())) {
                entity.setReuseCount(entity.getReuseCount() + 1);
                return entity;
            }
        }
        return null;
    }

    private PasswordReuseStatistic updatePasswordReuseStatistic(Long accountId, String hashedPassword, String newHashedPassword) {
        List<PasswordReuseStatistic> reuseStatisticList = passwordReuseStatisticRepository.findAllByAccountId(accountId);

        for (PasswordReuseStatistic entity : reuseStatisticList) {
            if (passwordEncoder.matches(hashedPassword, entity.getPasswordHash())) {
                if (entity.getReuseCount() >= 1) {
                    entity.setReuseCount(entity.getReuseCount() - 1);
                    return entity;
                } else if (entity.getReuseCount() == 0) {
                    return entity;
                }
            }

            if (passwordEncoder.matches(newHashedPassword, entity.getPasswordHash())) {
                entity.setReuseCount(entity.getReuseCount() + 1);
                return entity;
            }
        }
        return null;
    }

    public Keychain generateSecureKeychain(CreatePasswordRequest request, Accounts account) {
        try {
            SecretKey randomKey = envelopeEncryption.generateKey();
            IvParameterSpec vector = envelopeEncryption.generateIv();
            byte[] encryptedPass = envelopeEncryption.encrypt(request.getPassword(), randomKey, vector);

            EncryptionKey encryptionKey = new EncryptionKey(
                    Base64.getEncoder().encodeToString(randomKey.getEncoded()),
                    vector.getIV());

            Keychain keychain = new Keychain(
                    account,
                    encryptionKey,
                    request.getUsername(),
                    request.getDomain(),
                    encryptedPass);
            if (!request.getNotes().isEmpty()) {
                keychain.setNotes(request.getNotes());
            }
            PasswordExpiryPolicy policy = passwordExpiryPolicyRepository.findById(request.getPasswordExpiryPolicy())
                    .orElseThrow(() -> new ApiRequestException("Please choose 1 of the following policies.", HttpStatus.BAD_REQUEST));
            keychain.setPolicy(policy);
            return keychain;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            logger.error("generateSecureDataKey() error:", e);
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private PasswordChangeLog createPasswordChangeLog(Keychain keychain) {
        PasswordChangeLog passwordChangeLog = new PasswordChangeLog();
        passwordChangeLog.setKeychain(keychain);
        return passwordChangeLog;
    }

    @Transactional
    public void updateSecureDataKey(Accounts account, Keychain oldKeychain, EncryptionKey oldEncryptionKey, String password) {
        try {
            // SecureDataKeyResult result = new SecureDataKeyResult();
            IvParameterSpec regeneratedVector = envelopeEncryption.regenerateIvFromBytes(
                    oldKeychain.getEncryptionKey().getVector());
            byte[] decodedBytes = Base64.getDecoder().decode(oldKeychain.getEncryptionKey().getDek());
            SecretKey secretKey = new SecretKeySpec(decodedBytes, "AES");

            String decryptedPassword = envelopeEncryption.decrypt(
                    oldKeychain.getPassword(), secretKey, regeneratedVector);

            if (password.equals(decryptedPassword)) {
                throw new ApiRequestException(SAME_SAVED_PASSWORD, HttpStatus.BAD_REQUEST);
            }
            String hashedPassword = passwordEncoder.encode(password);

            PasswordReuseStatistic passwordReuseStatistic = updatePasswordReuseStatistic(
                    account.getId(), passwordEncoder.encode(decryptedPassword), hashedPassword
            );

            if (passwordReuseStatistic == null) {
                 passwordReuseStatistic = new PasswordReuseStatistic(account, hashedPassword);
            }
            passwordReuseStatisticRepository.save(passwordReuseStatistic);

            oldKeychain.getChangeLogs().add(createPasswordChangeLog(oldKeychain));

            SecretKey randomKey = envelopeEncryption.generateKey();
            IvParameterSpec newVector = envelopeEncryption.generateIv();
            byte[] encryptedPass = envelopeEncryption.encrypt(password, randomKey, newVector);

            oldKeychain.setPassword(encryptedPass);
            oldEncryptionKey.setDek(Base64.getEncoder().encodeToString(randomKey.getEncoded()));
            oldEncryptionKey.setVector(newVector.getIV());
            oldKeychain.setEncryptionKey(oldEncryptionKey);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            logger.error("updateSecureDataKey() error: ", e);
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String decryptPassword(DecryptedPasswordProjection userPassword) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(userPassword.getEncryptionKey().getDek());
            SecretKey secretKey = new SecretKeySpec(decodedBytes, "AES");
            IvParameterSpec vector = new IvParameterSpec(userPassword.getEncryptionKey().getVector());

            return envelopeEncryption.decrypt(userPassword.getPassword(), secretKey, vector);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException |
                 InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            System.out.println(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
