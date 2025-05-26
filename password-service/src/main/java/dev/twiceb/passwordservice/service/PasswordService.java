package dev.twiceb.passwordservice.service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import dev.twiceb.passwordservice.dto.request.*;
import dev.twiceb.passwordservice.repository.projection.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

public interface PasswordService {

        void updateTagsOnPassword(UUID passwordId, Set<Long> tags, BindingResult bindingResult);
        void favoritePassword(UUID passwordId, boolean isFavorite, BindingResult bindingResult);
        Map<String, String> updateUsername(UUID passwordId, String username, BindingResult bindingResult);
        Map<String, String> updatePasswordOnly(UUID passwordId, String password, BindingResult bindingResult);
        Map<String, String> updatePasswordNotes(UUID passwordId, String notes, BindingResult bindingResult);
        Map<String, String> createNewPassword(CreatePasswordRequest request, BindingResult bindingResult);
        Page<BaseKeychainProjection> getPasswords(Pageable pageable);
        KeychainProjection getPassword(UUID keychainId);
        Page<KeychainProjection> getExpiringPasswords(Pageable pageable);
        Page<KeychainProjection> getRecentPasswords(Pageable pageable);
        Map<String, String> getDecryptedPassword(UUID passwordId);
        Map<String, String> generateSecurePassword(int length);
        Map<String, String> deletePassword(UUID passwordId);
        Map<String, String> deleteAllPasswords();
        Page<KeychainProjection> searchPasswordsByQuery(SearchQueryRequest request,
                                                        BindingResult bindingResult, Pageable pageable);
        Page<EncryptionKeyPrincipleProjection> getEncryptionKeys(Pageable pageable);
}
