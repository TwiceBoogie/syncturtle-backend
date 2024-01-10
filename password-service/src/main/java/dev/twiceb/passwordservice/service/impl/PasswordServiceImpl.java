package dev.twiceb.passwordservice.service.impl;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.passwordservice.model.Accounts;
import dev.twiceb.passwordservice.model.EncryptionKey;
import dev.twiceb.passwordservice.model.Keychain;
import dev.twiceb.passwordservice.repository.AccountsRepository;
import dev.twiceb.passwordservice.repository.EncryptionKeyRepository;
import dev.twiceb.passwordservice.service.util.SecureDataKeyResult;
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
    private final AccountsRepository accountsRepository;

    @Override
    public Map<String, String> testingStuff() {
        Keychain keychain = keychainRepository.findById(1L)
                .orElseThrow(() -> new ApiRequestException(ACCOUNT_ALREADY_VERIFIED));
        System.out.println(keychain);
        System.out.println(keychain.getStatus());
        KeychainProjection kc = keychainRepository.getPasswordByDomain(1L, "Google.com", KeychainProjection.class)
                .orElseThrow(() -> new ApiRequestException(ACCOUNT_ALREADY_VERIFIED));
        System.out.println(kc);
        System.out.println("domain: " + kc.getDomain());

        return Map.of("message", "test");
    }

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

        Keychain keychain = passwordHelperService.generateSecureDataKey(
                request.getPassword(), request.getDomain(), account
        );
        keychainRepository.save(keychain);

        return Map.of("message", "Password saved for " + request.getDomain());
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

        Keychain keychainFromDb = keychainRepository
                .getPasswordByDomain(userAccount.getId(), request.getDomain(), Keychain.class)
                .orElseThrow(() -> new ApiRequestException(NO_PASSWORD_FOR_DOMAIN, HttpStatus.NOT_FOUND));

        Keychain updatedKeychain = passwordHelperService.updateSecureDataKey(
                keychainFromDb, keychainFromDb.getEncryptionKey(), request.getPassword()
        );
        keychainRepository.save(updatedKeychain);

        return Map.of("message", "New Password saved for " + request.getDomain());
    }

    @Override
    public Map<String, String> getDecryptedPassword(Long userId, Long passwordId) {
        Accounts userAccount = fetchAccount(userId);
        DecryptedPasswordProjection userPassword = keychainRepository.getPasswordById(userAccount.getId(), passwordId);

        return Map.of("message", passwordHelperService.decryptPassword(userPassword));
    }

    private Page<KeychainProjection> fetchKeychain(Long userId, Pageable pageable, String grabInstance) {
        Accounts userAccount = fetchAccount(userId);

        return switch (grabInstance) {
            case "Expired" -> keychainRepository.getPasswordsExpiringSoon(userAccount.getId(), pageable,
                    KeychainProjection.class);
            case "Recent" ->
                    keychainRepository.getRecentPasswords(userAccount.getId(), pageable, KeychainProjection.class);
            default -> keychainRepository.findAllByAccountId(userAccount.getId(), pageable, KeychainProjection.class);
        };
    }

    private Accounts fetchAccount(Long userId) {
        Accounts userAccount = accountsRepository.findAccountByUserId(userId, Accounts.class)
                .orElseThrow(() -> new ApiRequestException(AUTHENTICATION_ERROR, HttpStatus.UNAUTHORIZED));
        if (!userAccount.getUserStatus().equals("Active")) {
            throw new ApiRequestException(AUTHORIZATION_ERROR, HttpStatus.FORBIDDEN);
        }
        return userAccount;
    }
}
