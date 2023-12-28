package dev.twiceb.passwordservice.service;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import dev.twiceb.passwordservice.dto.request.CreatePasswordRequest;
import dev.twiceb.passwordservice.dto.request.UpdatePasswordRequest;
import dev.twiceb.passwordservice.repository.projection.KeychainProjection;
import dev.twiceb.passwordservice.repository.projection.UserPasswordProjection;

public interface PasswordService {

    Map<String, String> createPasswordForDomain(Long userId, CreatePasswordRequest request,
            BindingResult bindingResult);

    Page<KeychainProjection> getPasswords(Long userId, Pageable pageable);

    Page<KeychainProjection> getExpiringPasswords(Long userId, Pageable pageable);

    Page<KeychainProjection> getRecentPasswords(Long userId, Pageable pageable);

    Map<String, String> updatePasswordForDomain(Long userId, UpdatePasswordRequest request,
            BindingResult bindingResult);

    Map<String, String> getDecryptedPassword(Long userId, Long passwordId);

}
