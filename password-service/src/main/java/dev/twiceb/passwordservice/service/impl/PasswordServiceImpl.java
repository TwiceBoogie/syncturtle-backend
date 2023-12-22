package dev.twiceb.passwordservice.service.impl;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Map;

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
import dev.twiceb.passwordservice.repository.projection.UserPasswordProjection;
import dev.twiceb.passwordservice.service.PasswordService;
import dev.twiceb.passwordservice.service.util.EnvelopeEncryption;
import dev.twiceb.passwordservice.service.util.PasswordHelperService;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

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
    public Map<String, String> createPasswordForDomain(Long userId, CreatePasswordRequest request, BindingResult bindingResult) {
        passwordHelperService.processInputErrors(bindingResult);
        passwordHelperService.processPassword(request.getPassword(), request.getConfirmPassword());

        Accounts account = accountsRepository.findAccountByUserId(userId, Accounts.class)
                .orElseThrow(() -> new ApiRequestException(UNAUTHORIZED, HttpStatus.UNAUTHORIZED));

        if (keychainRepository.CheckIfDomainExist(account.getId(), request.getDomain())) {
            throw new ApiRequestException(DOMAIN_ALREADY_EXIST, HttpStatus.BAD_REQUEST);
        }

        try {
            SecretKey randomKey = envelopeEncryption.generateKey();
            IvParameterSpec vector = envelopeEncryption.generateIv();
            byte[] encryptedPass = envelopeEncryption.encrypt(request.getPassword(), randomKey, vector);

            EncryptionKey encryptionKey = new EncryptionKey(
                    Base64.getEncoder().encodeToString(randomKey.getEncoded()),
                    vector.getIV()
            );
            EncryptionKey savedEncryptionKey = encryptionKeyRepository.save(encryptionKey);

            Keychain keychain = new Keychain(
                    account,
                    savedEncryptionKey,
                    request.getDomain(),
                    encryptedPass
            );
            keychainRepository.save(keychain);

            return Map.of("message", "Password saved for ." + request.getDomain());
        } catch (Exception e) {
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Page<UserPasswordProjection> getPasswords(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPasswords'");
    }

    @Override
    public Page<UserPasswordProjection> getExpiringPasswords(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getExpiringPasswords'");
    }

    @Override
    public Page<UserPasswordProjection> getRecentPasswords(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRecentPasswords'");
    }

    @Override
    public Map<String, String> updatePasswordForDomain(UpdatePasswordRequest request, BindingResult bindingResult) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updatePasswordForDomain'");
    }

    @Override
    public Map<String, String> getDecryptedPassword(Long passwordId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDecryptedPassword'");
    }

}
