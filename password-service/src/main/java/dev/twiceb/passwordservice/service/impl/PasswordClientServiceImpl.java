package dev.twiceb.passwordservice.service.impl;

import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.common.enums.TimePeriod;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.common.util.AuthUtil;
import dev.twiceb.passwordservice.dto.response.CategoryListResponse;
import dev.twiceb.passwordservice.dto.response.ExpiryPoliciesResponse;
import dev.twiceb.passwordservice.dto.response.PasswordVaultHealthResponse;
import dev.twiceb.passwordservice.repository.*;
import dev.twiceb.passwordservice.repository.projection.*;
import dev.twiceb.passwordservice.service.PasswordClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static dev.twiceb.common.constants.ErrorMessage.NO_DOMAIN_PASSWORDS;

@Service
@RequiredArgsConstructor
public class PasswordClientServiceImpl implements PasswordClientService {

    private final KeychainRepository keychainRepository;
    private final EncryptionKeyRepository encryptionKeyRepository;
    private final CategoryRepository categoryRepository;
    private final RotationPolicyRepository rotationPolicyRepository;
    private final UserRepository userRepository;
    private final PasswordComplexityMetricRepository passwordComplexityMetricRepository;
    private final PasswordUpdateStatRepository passwordUpdateStatRepository;
    private final BasicMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public boolean isPasswordVaultEmpty() {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return encryptionKeyRepository.isPasswordVaultEmpty(authUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public HeaderResponse<CategoryListResponse> getCategories() {
        List<CategoryProjection> categories = categoryRepository.findAllReadOnly();
        return mapper.getHeaderResponse(categories, categories.size(), CategoryListResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public HeaderResponse<ExpiryPoliciesResponse> getExpiryPolicies() {
        List<ExpiryPolicyProjection> policies = rotationPolicyRepository.findAllPolicies();
        return mapper.getHeaderResponse(policies, policies.size(), ExpiryPoliciesResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public PasswordVaultHealthResponse getPasswordVaultHealth() {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        if (keychainRepository.countKeychainByEncryptionKey_User_Id(authUserId) == 0) {
            throw new ApiRequestException(NO_DOMAIN_PASSWORDS, HttpStatus.NOT_FOUND);
        }
        return mapper.convertToResponse(
                userRepository.getUsersVaultHealth(authUserId),
                PasswordVaultHealthResponse.class
        );
    }

    @Override
    public List<LocalDateTime> getKeychainCreationDate(String interval) {
        return null;
    }

    @Override
    public List<PasswordComplexityMetricProjection> getPasswordComplexityMetrics() {
        return passwordComplexityMetricRepository.findAllPCM();
    }

    @Override
    public List<KeychainNotificationProjection> getExpiringKeychains() {
        return null;
    }

    @Override
    public Page<PasswordUpdateStatProjection> getPolicyStatistics(TimePeriod timePeriod, Pageable pageable) {
        return passwordUpdateStatRepository.findAllByTimePeriod(timePeriod, pageable);
    }
}
