package dev.twiceb.passwordservice.service.util;

import dev.twiceb.common.util.EnvelopeEncryption;
import dev.twiceb.passwordservice.model.*;
import dev.twiceb.passwordservice.repository.*;
import dev.twiceb.passwordservice.repository.projection.DecryptedPasswordProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.mapper.FieldErrorMapper;
import dev.twiceb.common.mapper.FieldErrorMapper.ValidationContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordHelperService {

    // private final EncryptionKeyRepository encryptionKeyRepository;
    private final PasswordReuseStatisticRepository passwordReuseStatisticRepository;
    private final CategoryRepository categoryRepository;
    private final EnvelopeEncryption envelopeEncryption;
    private final RotationPolicyRepository rotationPolicyRepository;
    private final PasswordEncoder passwordEncoder;

    public void processInputErrors(BindingResult result, ValidationContext ctx) {
        if (result.hasErrors()) {
            throw FieldErrorMapper.mapToAuthException(result, ctx);
        }
    }

    public void processPassword(String password1, String password2) {
        if (!password1.equals(password2)) {
            throw new ApiRequestException(PASSWORDS_NOT_MATCH, HttpStatus.BAD_REQUEST);
        }

        if (password1.length() < 9) {
            throw new ApiRequestException(PASSWORD_LENGTH_ERROR, HttpStatus.BAD_REQUEST);
        }
    }

    public String generateSecurePassword(int length) {
        String alphanumericChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String specialChars = "!@#$%^&*()-_=+[]{}|;:,./?";
        String allChars = alphanumericChars + specialChars;
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        // Ensure at least one alphanumeric and one special character in the password
        password.append(alphanumericChars.charAt(random.nextInt(alphanumericChars.length())));
        password.append(specialChars.charAt(random.nextInt(specialChars.length())));

        // Generate the rest of the password
        for (int i = 2; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        return password.toString();
    }

    public RotationPolicy selectRotationPolicy(Long id) {
        return rotationPolicyRepository.findById(id).orElseThrow(() -> new ApiRequestException(
                "Please choose 1 of the following policies.", HttpStatus.BAD_REQUEST));
    }

    public List<Category> findAllCategories(Set<Long> tags) {
        return categoryRepository.findAllById(tags);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    // changed from 'for-each' to 'iterator' because of
    // 'ConcurrentModificationException'
    // removing an element from list disrupts the iterator used by the loop.
    public boolean searchAndUpdatePasswordReuseStatistic(
            List<PasswordReuseStatistic> passwordReuseStatistics, String decryptedPassword,
            String password) {
        boolean foundMatchingNewPassword = false;
        Iterator<PasswordReuseStatistic> iterator = passwordReuseStatistics.iterator();

        while (iterator.hasNext()) {
            PasswordReuseStatistic entity = iterator.next();
            // search for the reuse statistic entity that is tied to the old one.
            if (decryptedPassword != null
                    && passwordEncoder.matches(decryptedPassword, entity.getPasswordHash())) {
                if (entity.getReuseCount() >= 1) {
                    entity.setReuseCount(entity.getReuseCount() - 1);
                } else { // if it exists and the only one, then remove.
                    iterator.remove();
                }
            }
            // search reuse statistic entity if 1 already exist for the new password
            if (passwordEncoder.matches(password, entity.getPasswordHash())) {
                entity.setReuseCount(entity.getReuseCount() + 1);
                foundMatchingNewPassword = true;
            }
        }
        return foundMatchingNewPassword;
    }

    public SecretKey rebuildSecretKey(String dek, String algorithm) {
        byte[] decodedSecretKeyBytes = Base64.getDecoder().decode(dek);
        if (!(decodedSecretKeyBytes.length == 16 || decodedSecretKeyBytes.length == 24
                || decodedSecretKeyBytes.length == 32)) {
            throw new IllegalArgumentException(
                    "Invalid key length: " + decodedSecretKeyBytes.length);
        }
        return new SecretKeySpec(decodedSecretKeyBytes, algorithm);
    }

    private IvParameterSpec regenerateIvFromBytes(byte[] vector) {
        return envelopeEncryption.regenerateIvFromBytes(vector);
    }

    public IvParameterSpec generateNewIv() {
        return envelopeEncryption.generateIv();
    }

    public String decryptPassword(byte[] password, SecretKey secretKey, byte[] vector) {
        IvParameterSpec regeneratedVector = regenerateIvFromBytes(vector);
        return envelopeEncryption.decrypt(password, secretKey, regeneratedVector);
    }

    public byte[] encryptPassword(String password, SecretKey secretKey, IvParameterSpec vector) {
        return envelopeEncryption.encrypt(password, secretKey, vector);
    }

    public int countTotalPasswords(List<EncryptionKey> list) {
        int count = 0;
        for (EncryptionKey encryptionKey : list) {
            count += encryptionKey.getKeychains().size();
        }
        return count;
    }

    public int countTotalWeakPasswords(List<EncryptionKey> list) {
        int count = 0;
        for (EncryptionKey encryptionKey : list) {
            for (Keychain keychain : encryptionKey.getKeychains()) {
                if (keychain.getComplexityMetric().getPasswordComplexityScore() < 50) {
                    count++;
                }
            }
        }
        return count;
    }

    public int countTotalReusedPassword(Long userId) {
        return passwordReuseStatisticRepository.getTotalReuseCount(userId);
    }

    public double getAveragePasswordComplexityScore(List<EncryptionKey> list) {
        double sum = 0;
        for (EncryptionKey encryptionKey : list) {
            for (Keychain keychain : encryptionKey.getKeychains()) {
                sum += keychain.getComplexityMetric().getPasswordComplexityScore();
            }
        }
        return countTotalPasswords(list) / sum;
    }

    public double getVaultHealthPercentage(List<EncryptionKey> list, Long userId) {
        int totalPasswords = countTotalPasswords(list);
        int reusedScore = (countTotalReusedPassword(userId) / totalPasswords) * 100;
        int weakScore = (countTotalWeakPasswords(list) / totalPasswords) * 100;
        double weightedReusedScore = reusedScore * 0.5;
        double weightedWeakScore = weakScore * 0.3;
        double weightedAverageEntropy = getAveragePasswordComplexityScore(list) * 0.1;
        double negativeImpact = weightedReusedScore + weightedWeakScore + weightedAverageEntropy;
        return 100 - negativeImpact;
    }

    // @Transactional
    // public void updateSecureDataKey(Keychain keychainFromDb, String newPassword)
    // {
    // try {
    // EncryptionKey keyFromDb = keychainFromDb.getEncryptionKey();
    // byte[] decodedBytesSecretKey =
    // Base64.getDecoder().decode(keyFromDb.getDek());
    // SecretKey randomKey = new SecretKeySpec(decodedBytesSecretKey, "AES");
    // IvParameterSpec vector =
    // envelopeEncryption.regenerateIvFromBytes(keychainFromDb.getVector());
    // String decryptedPassword =
    // envelopeEncryption.decrypt(keychainFromDb.getPassword(), randomKey, vector);
    //
    // if (newPassword.equals(decryptedPassword)) {
    // throw new ApiRequestException(SAME_SAVED_PASSWORD, HttpStatus.CONFLICT);
    // }
    //
    // PasswordReuseStatistic passwordReuseStatistic = updatePasswordReuseStatistic(
    // keyFromDb.getId(), decryptedPassword, newPassword
    // );
    //
    // if (passwordReuseStatistic == null) {
    // passwordReuseStatistic = new PasswordReuseStatistic(keyFromDb,
    // passwordEncoder.encode(newPassword));
    // }
    // passwordReuseStatisticRepository.save(passwordReuseStatistic); // save the
    // updated or new one
    //
    // keychainFromDb.getChangeLogs().add(createPasswordChangeLog(keychainFromDb));
    // IvParameterSpec newVector = envelopeEncryption.generateIv();
    // byte[] encryptedPassword = envelopeEncryption.encrypt(newPassword, randomKey,
    // newVector);
    //
    // keychainFromDb.setPassword(encryptedPassword);
    // keyFromDb.setDek(Base64.getEncoder().encodeToString(randomKey.getEncoded()));
    // keychainFromDb.setVector(newVector.getIV());
    // keychainFromDb.setEncryptionKey(keyFromDb);
    //
    // } catch (Exception e) {
    // log.error("updateSecureDataKey() error: ", e);
    // throw new ApiRequestException(INTERNAL_SERVER_ERROR,
    // HttpStatus.INTERNAL_SERVER_ERROR);
    // }
    // }

    public String decryptPassword(DecryptedPasswordProjection userPassword) {
        try {
            byte[] decodedBytes =
                    Base64.getDecoder().decode(userPassword.getEncryptionKey().getDek());
            SecretKey secretKey = new SecretKeySpec(decodedBytes, "AES");
            IvParameterSpec vector =
                    envelopeEncryption.regenerateIvFromBytes(userPassword.getVector());

            return envelopeEncryption.decrypt(userPassword.getPassword(), secretKey, vector);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
