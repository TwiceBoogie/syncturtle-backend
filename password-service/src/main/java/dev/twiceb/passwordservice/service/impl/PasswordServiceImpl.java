package dev.twiceb.passwordservice.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.util.AuthUtil;
import dev.twiceb.passwordservice.broker.producer.PasswordChangeProducer;
import dev.twiceb.passwordservice.dto.request.*;
import dev.twiceb.passwordservice.model.*;
import dev.twiceb.passwordservice.repository.*;
import dev.twiceb.passwordservice.repository.projection.*;
import dev.twiceb.passwordservice.service.UserService;
import dev.twiceb.passwordservice.service.util.DictionaryCommonWords;
import dev.twiceb.passwordservice.service.util.PasswordComplexityAnalyzer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import dev.twiceb.passwordservice.service.PasswordService;
import dev.twiceb.passwordservice.service.util.PasswordHelperService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import static dev.twiceb.common.constants.ErrorMessage.*;
import static dev.twiceb.common.constants.PathConstants.AUTH_DEVICE_KEY_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final KeychainRepository keychainRepository;
    private final PasswordHelperService passwordHelperService;
    private final EncryptionKeyRepository encryptionKeyRepository;
    private final DictionaryCommonWords dictionaryCommonWords;
    private final PasswordChangeProducer passwordChangeProducer;
    private final OldPasswordRepository oldPasswordRepository;
    private final UserService userService;

    @Override
    @Transactional
    public void updateTagsOnPassword(UUID passwordId, Set<Long> tags, BindingResult bindingResult) {
        // passwordHelperService.processBindingResults(bindingResult);
        Keychain keychain = getValidatedKeychain(passwordId);
        List<Category> categories = passwordHelperService.findAllCategories(tags);
        if (categories.isEmpty()) {
            throw new ApiRequestException("Invalid tags", HttpStatus.BAD_REQUEST);
        }
        keychain.setCategories(categories);
        keychainRepository.save(keychain);
    }

    @Override
    @Transactional
    public void favoritePassword(UUID passwordId, boolean isFavorite, BindingResult bindingResult) {
        // passwordHelperService.processBindingResults(bindingResult);
        Keychain keychain = getValidatedKeychain(passwordId);
        keychain.setFavorite(isFavorite);
        keychainRepository.save(keychain);
    }

    @Override
    @Transactional
    public Map<String, String> updateUsername(UUID passwordId, String username,
            BindingResult bindingResult) {
        // passwordHelperService.processBindingResults(bindingResult);
        Keychain keychain = getValidatedKeychain(passwordId);
        keychain.setUsername(username);
        UUID deviceKeyId = getUserDeviceId();
        addAndSaveNewChangeLog(keychain, "keychain update", "username", deviceKeyId);
        return Map.of("message", "Username updated successfully.");
    }

    @Override
    @Transactional
    public Map<String, String> createNewPassword(CreatePasswordRequest request,
            BindingResult bindingResult) {
        // passwordHelperService.processBindingResults(bindingResult);
        UUID authUserId = AuthUtil.getAuthenticatedUserId();
        EncryptionKey key = selectAndValidateEncryptionKey(request.getEncryptionId(), authUserId);
        checkDomainAvailability(authUserId, request.getDomain());

        Keychain keychain = buildSecureKeychain(key, request);
        User user = key.getUser();
        validateAndUpdatePasswordReuseStatistic(user, null, request.getPassword());
        // TODO: refactor
        key.getKeychains().add(keychain);
        user.getEncryptionKeys().add(key);
        encryptionKeyRepository.save(key); // might need to be a userRep save

        return Map.of("message", "Password saved for " + request.getDomain());
    }

    @Override
    @Transactional
    public Map<String, String> updatePasswordNotes(UUID passwordId, String notes,
            BindingResult bindingResult) {
        // passwordHelperService.processBindingResults(bindingResult);
        Keychain keychain = getValidatedKeychain(passwordId);
        keychain.setNotes(notes);
        keychainRepository.save(keychain);
        return Map.of("message", "Notes updated successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BaseKeychainProjection> getPasswords(Pageable pageable) {
        return fetchKeychain(pageable, BaseKeychainProjection.class, "Generic");
    }

    @Override
    @Transactional(readOnly = true)
    public KeychainProjection getPassword(UUID keychainId) {
        return keychainRepository.findKeychainById(keychainId, AuthUtil.getAuthenticatedUserId())
                .orElseThrow(
                        () -> new ApiRequestException(NO_RESOURCE_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KeychainProjection> getExpiringPasswords(Pageable pageable) {
        return fetchKeychain(pageable, KeychainProjection.class, "Expired");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KeychainProjection> getRecentPasswords(Pageable pageable) {
        return fetchKeychain(pageable, KeychainProjection.class, "Recent");
    }

    @Override
    @Transactional
    public Map<String, String> updatePasswordOnly(UUID passwordId, String password,
            BindingResult bindingResult) {
        // passwordHelperService.processBindingResults(bindingResult);
        Keychain keychain = getValidatedKeychain(passwordId);
        User authUser = userService.getAuthUser();
        String decryptedPassword = decryptOldPassword(keychain);
        validateAndUpdatePasswordReuseStatistic(authUser, decryptedPassword, password);
        encryptNewPassword(keychain, password);
        UUID deviceKeyId = getUserDeviceId();
        addAndSaveNewChangeLog(keychain, "password update", "password", deviceKeyId);
        OldPasswordDTO oldPasswordDTO = storeOldPassword(keychain);
        passwordChangeProducer.sendPasswordChangeEvent(authUser.getId(),
                LocalDateTime.parse(oldPasswordDTO.getTimestamp()), deviceKeyId);
        return Map.of("message", "Password updated successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getDecryptedPassword(UUID passwordId) {
        UUID authUserId = AuthUtil.getAuthenticatedUserId();
        return keychainRepository.getPasswordById(authUserId, passwordId)
                .map(passwordHelperService::decryptPassword)
                .map(password -> Map.of("message", password)).orElseThrow(
                        () -> new ApiRequestException(NO_RESOURCE_FOUND, HttpStatus.NOT_FOUND));
    }

    @Override
    public Map<String, String> generateSecurePassword(int length) {
        if (length < 8) {
            throw new ApiRequestException(GENERATED_PASSWORD_LENGTH, HttpStatus.BAD_REQUEST);
        }
        return Map.of("message", passwordHelperService.generateSecurePassword(length));
    }

    @Override
    @Transactional
    public Map<String, String> deletePassword(UUID passwordId) {
        Keychain keychain = getValidatedKeychain(passwordId);
        EncryptionKey key = keychain.getEncryptionKey();
        validateOwnershipOfKeychain(keychain);
        key.getKeychains().remove(keychain);
        encryptionKeyRepository.save(key);
        return Map.of("message", "Password deleted Successfully.");
    }

    @Override
    @Transactional
    public Map<String, String> deleteAllPasswords() {
        User authUser = userService.getAuthUser();
        List<EncryptionKey> keys = authUser.getEncryptionKeys();
        for (EncryptionKey key : keys) {
            key.getKeychains().removeAll(key.getKeychains());
        }
        encryptionKeyRepository.saveAll(keys);
        return Map.of("message", "All passwords deleted successfully.");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<KeychainProjection> searchPasswordsByQuery(SearchQueryRequest request,
            BindingResult bindingResult, Pageable pageable) {
        // passwordHelperService.processBindingResults(bindingResult);
        UUID authUserId = AuthUtil.getAuthenticatedUserId();
        return keychainRepository.searchByQuery(request.getSearchQuery(), authUserId, pageable);
    }

    @Override
    @Transactional
    public Page<EncryptionKeyPrincipleProjection> getEncryptionKeys(Pageable pageable) {
        return encryptionKeyRepository.getEncryptionKeyByUserId(AuthUtil.getAuthenticatedUserId(),
                pageable, EncryptionKeyPrincipleProjection.class);
    }

    @SuppressWarnings("unused")
    private long parseTTL(String ttl) {
        // Parse TTL string (e.g., "1h") into minutes or appropriate time unit
        // This is a simplified example, more robust parsing might be needed
        if (ttl.endsWith("h")) {
            return Long.parseLong(ttl.replace("h", "")) * 60;
        }
        return 0;
    }

    private OldPasswordDTO storeOldPassword(Keychain keychain) {
        // VaultKeyValueOperations operations = vaultTemplate.opsForKeyValue("secret",
        // VaultKeyValueOperationsSupport.KeyValueBackend.KV_2);
        // StringBuilder completeSecretPath = new StringBuilder();
        // completeSecretPath.append(VAULT_PATH).append(username).append("/").append(keychain.getId());
        //
        OldPasswordDTO oldPasswordDTO = new OldPasswordDTO();
        oldPasswordDTO.setId(String.valueOf(keychain.getId()));
        oldPasswordDTO.setPassword(Base64.getEncoder().encodeToString(keychain.getPassword()));
        oldPasswordDTO.setDekId(String.valueOf(keychain.getEncryptionKey().getId()));
        oldPasswordDTO.setTtl("1h");
        oldPasswordDTO.setVector(Base64.getEncoder().encodeToString(keychain.getVector()));
        return oldPasswordRepository.save(oldPasswordDTO);
        // operations.put(completeSecretPath.toString(), oldPasswordDTO);
        // return oldPasswordDTO;

    }

    private EncryptionKey selectAndValidateEncryptionKey(UUID id, UUID authUserId) {
        EncryptionKey key = encryptionKeyRepository.findById(id).orElseThrow(
                () -> new ApiRequestException(NO_RESOURCE_FOUND, HttpStatus.NOT_FOUND));
        validateOwnershipOfEncryptionKey(key, authUserId);
        return key;
    }

    private void checkDomainAvailability(UUID authUserId, String domain) {
        if (keychainRepository.CheckIfDomainExist(authUserId, domain)) {
            throw new ApiRequestException(DOMAIN_ALREADY_EXIST, HttpStatus.CONFLICT);
        }
    }

    private String decryptOldPassword(Keychain keychain) {
        EncryptionKey key = keychain.getEncryptionKey();
        SecretKey secretKey =
                passwordHelperService.rebuildSecretKey(key.getDek(), key.getAlgorithm());
        return passwordHelperService.decryptPassword(keychain.getPassword(), secretKey,
                keychain.getVector());
    }

    private Keychain getValidatedKeychain(UUID passwordId) {
        Keychain keychain = keychainRepository.findById(passwordId).orElseThrow(
                () -> new ApiRequestException(NO_RESOURCE_FOUND, HttpStatus.NOT_FOUND));
        validateOwnershipOfKeychain(keychain);
        return keychain;
    }

    private void validateAndUpdatePasswordReuseStatistic(User user, String passwordFromDb,
            String password) {
        if (password.equals(passwordFromDb)) {
            throw new ApiRequestException(SAME_SAVED_PASSWORD, HttpStatus.CONFLICT);
        }
        List<PasswordReuseStatistic> passwordReuseStatistics = user.getPasswordReuseStatistics();
        if (!passwordHelperService.searchAndUpdatePasswordReuseStatistic(passwordReuseStatistics,
                passwordFromDb, password)) {
            PasswordReuseStatistic passwordReuseStatistic = new PasswordReuseStatistic(user,
                    passwordHelperService.encodePassword(password));
            passwordReuseStatistics.add(passwordReuseStatistic);
        }
        user.setPasswordReuseStatistics(passwordReuseStatistics); // do I need this here?
    }

    private void addAndSaveNewChangeLog(Keychain keychain, String reason, String type,
            UUID deviceKeyId) {
        PasswordChangeLog passwordChangeLog = new PasswordChangeLog();
        passwordChangeLog.setKeychain(keychain);
        passwordChangeLog.setChangeReason(reason);
        passwordChangeLog.setChangeType(type);
        passwordChangeLog.setUserDeviceId(deviceKeyId);
        keychain.getChangeLogs().add(passwordChangeLog);
        keychainRepository.save(keychain);
    }

    private void encryptNewPassword(Keychain keychain, String password) {
        EncryptionKey key = keychain.getEncryptionKey();
        SecretKey secretKey =
                passwordHelperService.rebuildSecretKey(key.getDek(), key.getAlgorithm());
        IvParameterSpec newVector = passwordHelperService.generateNewIv();
        keychain.setPassword(passwordHelperService.encryptPassword(password, secretKey, newVector));
        key.setDek(Base64.getEncoder().encodeToString(secretKey.getEncoded()));
        keychain.setVector(newVector.getIV());
        keychain.setEncryptionKey(key); // again do I need to set it? just an update
    }

    private Keychain buildSecureKeychain(EncryptionKey key, CreatePasswordRequest request) {
        SecretKey secretKey =
                passwordHelperService.rebuildSecretKey(key.getDek(), key.getAlgorithm());
        IvParameterSpec vector = passwordHelperService.generateNewIv();
        byte[] encryptedPassword =
                passwordHelperService.encryptPassword(request.getPassword(), secretKey, vector);
        // String encryptedPasswordBase64 =
        // Base64.getEncoder().encodeToString(encryptedPassword);
        // String vectorBase64 = Base64.getEncoder().encodeToString(vector.getIV());
        Keychain keychain = buildKeychain(key, request, encryptedPassword, vector);
        return updateKeychainAttributes(keychain, request);
    }

    private Keychain buildKeychain(EncryptionKey key, CreatePasswordRequest request,
            byte[] encryptedPassword, IvParameterSpec vector) {
        return new Keychain(key, request.getUsername(), request.getDomain(),
                request.getWebsiteUrl(), encryptedPassword, vector.getIV());
    }

    private Keychain updateKeychainAttributes(Keychain keychain, CreatePasswordRequest request) {
        if (!request.getNotes().isEmpty()) {
            keychain.setNotes(request.getNotes());
        }
        keychain.getCategories()
                .addAll(passwordHelperService.findAllCategories(request.getCategory()));
        keychain.setComplexityMetric(generatePasswordComplexityMetric(request.getPassword()));
        keychain.getComplexityMetric().setKeychain(keychain);
        keychain.setRotationPolicy(selectRotationPolicy(request.getPasswordExpiryPolicy()));
        return keychain;
    }

    private PasswordComplexityMetric generatePasswordComplexityMetric(String rawPassword) {
        return PasswordComplexityAnalyzer.analyzePassword(rawPassword,
                dictionaryCommonWords.getHashedDictionaryWords());
    }

    private RotationPolicy selectRotationPolicy(Long id) {
        return passwordHelperService.selectRotationPolicy(id);
    }

    private <T> Page<T> fetchKeychain(Pageable pageable, Class<T> clazz, String grabInstance) {
        User userAccount = userService.getAuthUser();

        return switch (grabInstance) {
            case "Expired" -> keychainRepository.getPasswordsExpiringSoon(userAccount.getId(),
                    pageable, clazz);
            case "Recent" -> keychainRepository.getRecentPasswords(userAccount.getId(), pageable,
                    clazz);
            default -> keychainRepository.findAllByAccountId(userAccount.getId(), pageable, clazz);
        };
    }

    private void validateOwnershipOfKeychain(Keychain keychain) {
        UUID authUserId = AuthUtil.getAuthenticatedUserId();
        validateOwnershipOfEncryptionKey(keychain.getEncryptionKey(), authUserId);
    }

    private void validateOwnershipOfEncryptionKey(EncryptionKey key, UUID authUserId) {
        if (!key.getUser().getId().equals(authUserId)) {
            throw new ApiRequestException(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
    }

    private UUID getUserDeviceId() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        @SuppressWarnings("null")
        HttpServletRequest request = ((ServletRequestAttributes) attributes).getRequest();
        return UUID.fromString(request.getHeader(AUTH_DEVICE_KEY_ID));
    }
}
