package dev.twiceb.passwordservice.service.util;

import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.model.Accounts;
import dev.twiceb.passwordservice.model.EncryptionKey;
import dev.twiceb.passwordservice.model.Keychain;
import dev.twiceb.passwordservice.repository.EncryptionKeyRepository;
import dev.twiceb.passwordservice.repository.KeychainRepository;
import dev.twiceb.passwordservice.repository.projection.DecryptedPasswordProjection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.util.ServiceHelper;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Component
@RequiredArgsConstructor
public class PasswordHelperService extends ServiceHelper {

    @PersistenceContext
    private final EntityManager entityManager;
    private final EnvelopeEncryption envelopeEncryption;

    public void processPassword(String password1, String password2) {
        if (!password1.equals(password2)) {
            throw new ApiRequestException(PASSWORDS_NOT_MATCH, HttpStatus.BAD_REQUEST);
        }

        if (password1.length() < 9) {
            throw new ApiRequestException(PASSWORD_LENGTH_ERROR, HttpStatus.BAD_REQUEST);
        }
    }

    public Keychain generateSecureDataKey(String password, String domain, Accounts account) {
        try {
            SecureDataKeyResult res = new SecureDataKeyResult();
            SecretKey randomKey = envelopeEncryption.generateKey();
            IvParameterSpec vector = envelopeEncryption.generateIv();
            byte[] encryptedPass = envelopeEncryption.encrypt(password, randomKey, vector);

            EncryptionKey encryptionKey = new EncryptionKey(
                    Base64.getEncoder().encodeToString(randomKey.getEncoded()),
                    vector.getIV());

            return new Keychain(
                    account,
                    encryptionKey,
                    domain,
                    encryptedPass);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Keychain updateSecureDataKey(Keychain oldKeychain, EncryptionKey oldEncryptionKey, String password) {
        try {
            SecureDataKeyResult result = new SecureDataKeyResult();
            IvParameterSpec regeneratedVector = envelopeEncryption.regenerateIvFromBytes(
                    oldKeychain.getEncryptionKey().getVector()
            );
            byte[] decodedBytes = Base64.getDecoder().decode(oldKeychain.getEncryptionKey().getVector());
            SecretKey secretKey = new SecretKeySpec(decodedBytes, "AES");

            String decryptedPassword = envelopeEncryption.decrypt(
                    oldKeychain.getPassword(), secretKey, regeneratedVector
            );

            if (password.equals(decryptedPassword)) {
                throw new ApiRequestException(SAME_SAVED_PASSWORD, HttpStatus.BAD_REQUEST);
            }

            SecretKey randomKey = envelopeEncryption.generateKey();
            IvParameterSpec newVector = envelopeEncryption.generateIv();
            byte[] encryptedPass = envelopeEncryption.encrypt(password, randomKey, newVector);

            oldKeychain.setPassword(encryptedPass);
            oldEncryptionKey.setDek(Base64.getEncoder().encodeToString(randomKey.getEncoded()));
            oldEncryptionKey.setVector(newVector.getIV());
            oldKeychain.setEncryptionKey(oldEncryptionKey);

            return oldKeychain;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String decryptPassword(DecryptedPasswordProjection userPassword) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(userPassword.getEncryptionKey().getDek());
            SecretKey secretKey = new SecretKeySpec(decodedBytes, "AES");
            IvParameterSpec vector = new IvParameterSpec(userPassword.getEncryptionKey().getVector());

            return envelopeEncryption.decrypt(decodedBytes, secretKey, vector);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
