package dev.twiceb.passwordservice.service.impl;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.passwordservice.dto.request.GenerateRandomPasswordRequest;
import dev.twiceb.passwordservice.model.Accounts;
import dev.twiceb.passwordservice.model.Keychain;
import dev.twiceb.passwordservice.repository.AccountsRepository;
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
import dev.twiceb.passwordservice.service.util.PasswordHelperService;
import lombok.RequiredArgsConstructor;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final KeychainRepository keychainRepository;
    private final AccountsRepository accountsRepository;
    private final PasswordHelperService passwordHelperService;

    @Override
    @Transactional
    public Map<String, String> createNewPassword(Long userId, CreatePasswordRequest request,
                                                 BindingResult bindingResult) {
        passwordHelperService.processBindingResults(bindingResult).processPassword(
                request.getPassword(), request.getConfirmPassword()
        );

        Accounts account = fetchAccount(userId);

        if (keychainRepository.CheckIfDomainExist(account.getId(), request.getDomain())) {
            throw new ApiRequestException(DOMAIN_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
        }

        Keychain keychain = passwordHelperService.generateSecureKeychain(request, account);
        keychain.setUserPasswordExpirySetting(
                passwordHelperService.createUserPasswordExpirySetting(request.getPasswordExpiryPolicy())
        );
        keychain.getUserPasswordExpirySetting().setKeychain(keychain);

        passwordHelperService.buildAnalyticEntities(keychain, request.getPassword());

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
        passwordHelperService.processBindingResults(bindingResult)
                .processPassword(request.getPassword(), request.getConfirmPassword());

        Keychain keychainFromDb = keychainRepository.findById(request.getId()).orElseThrow(
                () -> new ApiRequestException(NO_RESOURCE_FOUND, HttpStatus.NOT_FOUND)
        );

        Accounts account = keychainFromDb.getAccount();

        if (!account.getUserId().equals(userId)) {
            throw new ApiRequestException(AUTHENTICATION_ERROR, HttpStatus.UNAUTHORIZED);
        } else if (!account.getUserStatus().equals("Active")) {
            throw new ApiRequestException(AUTHORIZATION_ERROR, HttpStatus.FORBIDDEN);
        }

        passwordHelperService.updateSecureDataKey(
                account, keychainFromDb, keychainFromDb.getEncryptionKey(), request.getPassword());

        keychainRepository.save(keychainFromDb);

        return Map.of("message", "Username and/or Password updated successfully.");
    }

    @Override
    public Map<String, String> getDecryptedPassword(Long userId, Long passwordId) {
        Accounts userAccount = fetchAccount(userId);
        DecryptedPasswordProjection userPassword = keychainRepository.getPasswordById(userAccount.getId(), passwordId);

        return Map.of("message", passwordHelperService.decryptPassword(userPassword));
    }

    @Override
    public Map<String, String> generateSecurePassword(GenerateRandomPasswordRequest request) {
        String alphanumericChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String specialChars = "!@#$%^&*()-_=+[]{}|;:'\",.<>/?";
        int length = request.getRandomPasswordLength();
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

        return Map.of("message", password.toString());
    }

    @Override
    @Transactional
    public Map<String, String> deletePassword(Long userId, Long passwordId) {
        Keychain keychainToDelete = keychainRepository.findById(passwordId).orElseThrow(
                () -> new ApiRequestException(NO_RESOURCE_FOUND, HttpStatus.NOT_FOUND)
        );
        Accounts userAccount = keychainToDelete.getAccount();
        if (!userAccount.getUserId().equals(userId)) {
            throw new ApiRequestException(AUTHORIZATION_ERROR, HttpStatus.FORBIDDEN);
        }
        if (!userAccount.getUserStatus().equals("Active")) {
            throw new ApiRequestException(AUTHORIZATION_ERROR, HttpStatus.FORBIDDEN);
        }
        keychainRepository.delete(keychainToDelete);

        return Map.of("message", "Password deleted Successfully.");
    }

    @Override
    @Transactional
    public Map<String, String> deleteAllPasswords(Long userId) {
        Accounts userAccount = fetchAccount(userId);
        List<Keychain> keychainsToDelete = keychainRepository.findAllKeychain(userAccount.getId());
        keychainRepository.deleteAll(keychainsToDelete);

        return Map.of("message", "All passwords deleted successfully.");
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
        Accounts userAccount = accountsRepository.findAccountByUserId(userId);

        if (userAccount == null) {
            throw new ApiRequestException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }

        if (!userAccount.getUserStatus().equals("Active")) {
            throw new ApiRequestException(AUTHORIZATION_ERROR, HttpStatus.FORBIDDEN);
        }
        return userAccount;
    }
}
