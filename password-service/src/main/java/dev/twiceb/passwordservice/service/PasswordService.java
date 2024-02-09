package dev.twiceb.passwordservice.service;

import java.util.Map;

import dev.twiceb.passwordservice.dto.request.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import dev.twiceb.passwordservice.repository.projection.KeychainProjection;

public interface PasswordService {

        Map<String, String> createNewPassword(Long userId, CreatePasswordRequest request,
                                              BindingResult bindingResult);

        Page<KeychainProjection> getPasswords(Long userId, Pageable pageable);

        Page<KeychainProjection> getExpiringPasswords(Long userId, Pageable pageable);

        Page<KeychainProjection> getRecentPasswords(Long userId, Pageable pageable);

        Map<String, String> updatePassword(Long userId, UpdatePasswordRequest request,
                        BindingResult bindingResult);

        Map<String, String> getDecryptedPassword(Long userId, Long passwordId);

        Map<String, String> generateSecurePassword(int length);

        Map<String, String> deletePassword(Long userId, Long passwordId);

        Map<String, String> deleteAllPasswords(Long userId);

        Page<KeychainProjection> searchPasswordsByQuery(Long userId, SearchQueryRequest request,
                                                        BindingResult bindingResult, Pageable pageable);
}
