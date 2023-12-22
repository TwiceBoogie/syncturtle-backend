package dev.twiceb.passwordsservice.service.impl;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import dev.twiceb.passwordsservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordsservice.dto.request.UpdatePasswordRequest;
import dev.twiceb.passwordsservice.repository.KeychainRepository;
import dev.twiceb.passwordsservice.repository.projection.UserPasswordProjection;
import dev.twiceb.passwordsservice.service.PasswordService;
import dev.twiceb.passwordsservice.service.util.EnvelopeEncryption;
import dev.twiceb.passwordsservice.service.util.PasswordHelperService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final KeychainRepository keychainRepository;
    private final PasswordHelperService passwordHelperService;
    private final EnvelopeEncryption envelopeEncryption;

    @Override
    public Map<String, String> createPasswordForDomain(String userId, CreatePasswordRequest request, BindingResult bindingResult) {
        passwordHelperService.processInputErrors(bindingResult);
        passwordHelperService.processPassword(request.getPassword(), request.getConfirmPassword());


        throw new UnsupportedOperationException("Unimplemented method 'createPasswordForDomain'");
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
