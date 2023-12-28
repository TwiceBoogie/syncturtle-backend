package dev.twiceb.passwordservice.service.impl;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;
import java.util.List;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.passwordservice.model.Accounts;
import dev.twiceb.passwordservice.model.EncryptionKey;
import dev.twiceb.passwordservice.model.Keychain;
import dev.twiceb.passwordservice.repository.AccountsRepository;
import dev.twiceb.passwordservice.repository.EncryptionKeyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.dto.request.UpdatePasswordRequest;
import dev.twiceb.passwordservice.repository.KeychainRepository;
import dev.twiceb.passwordservice.repository.projection.DecryptedPasswordProjection;
import dev.twiceb.passwordservice.repository.projection.KeychainProjection;
import dev.twiceb.passwordservice.service.PasswordService;
import dev.twiceb.passwordservice.service.util.EnvelopeEncryption;
import dev.twiceb.passwordservice.service.util.PasswordHelperService;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final KeychainRepository keychainRepository;
    private final PasswordHelperService passwordHelperService;
    private final EnvelopeEncryption envelopeEncryption;
    private final AccountsRepository accountsRepository;
    private final EncryptionKeyRepository encryptionKeyRepository;

    @Override
    @Transactional
    public Map<String, String> createPasswordForDomain(Long userId, CreatePasswordRequest request,
            BindingResult bindingResult) {
        passwordHelperService.processInputErrors(bindingResult);
        passwordHelperService.processPassword(request.getPassword(), request.getConfirmPassword());

        Accounts account = fetchAccount(userId);

        if (keychainRepository.CheckIfDomainExist(account.getId(), request.getDomain())) {
            throw new ApiRequestException(DOMAIN_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
        }

        try {
            SecretKey randomKey = envelopeEncryption.generateKey();
            IvParameterSpec vector = envelopeEncryption.generateIv();
            byte[] encryptedPass = envelopeEncryption.encrypt(request.getPassword(), randomKey, vector);

            EncryptionKey encryptionKey = new EncryptionKey(
                    Base64.getEncoder().encodeToString(randomKey.getEncoded()),
                    vector.getIV());
            EncryptionKey savedEncryptionKey = encryptionKeyRepository.save(encryptionKey);

            Keychain keychain = new Keychain(
                    account,
                    savedEncryptionKey,
                    request.getDomain(),
                    encryptedPass);
            keychainRepository.save(keychain);

            return Map.of("message", "Password saved for " + request.getDomain());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<KeychainProjection> getPasswords(Long userId, Pageable pageable) {
        return fetchKeychain(userId, pageable, "Generic");
    }

    @Override
    public Page<KeychainProjection> getExpiringPasswords(Long userId, Pageable pageable) {
        return fetchKeychain(userId, pageable, "Expired");
    }

    @Override
    public Page<KeychainProjection> getRecentPasswords(Long userId, Pageable pageable) {
        return fetchKeychain(userId, pageable, "Recent");
    }

    @Override
    @Transactional
    public Map<String, String> updatePasswordForDomain(Long userId, UpdatePasswordRequest request,
            BindingResult bindingResult) {
        passwordHelperService.processInputErrors(bindingResult);
        passwordHelperService.processPassword(request.getPassword(), request.getConfirmPassword());

        Accounts userAccount = fetchAccount(userId);

        Keychain keychain = keychainRepository
                .getPasswordByDomain(userAccount.getId(), request.getDomain(), Keychain.class)
                .orElseThrow(() -> new ApiRequestException(NO_PASSWORD_FOR_DOMAIN, HttpStatus.NOT_FOUND));

        IvParameterSpec vector = envelopeEncryption.regenerateIvFromBytes(keychain.getEncryptionKey().getVector());
        byte[] decodedBytes = Base64.getDecoder().decode(keychain.getEncryptionKey().getVector());
        SecretKey secretKey = new SecretKeySpec(decodedBytes, "AES");
        try {
            String decryptedPassword = envelopeEncryption.decrypt(keychain.getPassword(), secretKey, vector);

            if (request.getPassword().equals(decryptedPassword)) {
                throw new ApiRequestException(SAME_SAVED_PASSWORD, HttpStatus.BAD_REQUEST);
            }

            SecretKey randomKey = envelopeEncryption.generateKey();
            IvParameterSpec newVector = envelopeEncryption.generateIv();
            byte[] encryptedPass = envelopeEncryption.encrypt(request.getPassword(), randomKey, newVector);

            keychain.setPassword(encryptedPass);

            EncryptionKey encryptionKey = encryptionKeyRepository.findById(keychain.getEncryptionKey().getId())
                    .orElseThrow(
                            () -> new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR));
            encryptionKey.setDek(Base64.getEncoder().encodeToString(randomKey.getEncoded()));
            encryptionKey.setVector(newVector.getIV());
            EncryptionKey savedEncryptionKey = encryptionKeyRepository.save(encryptionKey);
            keychain.setEncryptionKey(savedEncryptionKey);
            keychainRepository.save(keychain);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return Map.of("message", "New Password saved for " + request.getDomain());
    }

    @Override
    public Map<String, String> getDecryptedPassword(Long userId, Long passwordId) {
        Accounts userAccount = fetchAccount(userId);
        DecryptedPasswordProjection userPassword = keychainRepository.getPasswordById(userAccount.getId(), passwordId);

        byte[] decodedBytes = Base64.getDecoder().decode(userPassword.getEncryptionKey().getDek());
        SecretKey secretKey = new SecretKeySpec(decodedBytes, "AES");
        IvParameterSpec vector = new IvParameterSpec(userPassword.getEncryptionKey().getVector());

        try {
            String decryptedPassword = envelopeEncryption.decrypt(decodedBytes, secretKey, vector);
            return Map.of("message", decryptedPassword);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Page<KeychainProjection> fetchKeychain(Long userId, Pageable pageable, String grabInstance) {
        Accounts userAccount = fetchAccount(userId);

        switch (grabInstance) {
            case "Expired":
                return keychainRepository.getPasswordsExpiringSoon(userAccount.getId(), pageable,
                        KeychainProjection.class);
            case "Recent":
                return keychainRepository.getRecentPasswords(userAccount.getId(), pageable, KeychainProjection.class);
            case "Generic":
            default:
                return keychainRepository.findAllByAccountId(userAccount.getId(), pageable, KeychainProjection.class);
        }
    }

    private Accounts fetchAccount(Long userId) {
        return accountsRepository.findAccountByUserId(userId, Accounts.class)
                .orElseThrow(() -> new ApiRequestException(AUTHENTICATION_ERROR, HttpStatus.FORBIDDEN));
    }

}
