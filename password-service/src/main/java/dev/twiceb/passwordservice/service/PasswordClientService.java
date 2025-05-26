package dev.twiceb.passwordservice.service;

import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.common.enums.TimePeriod;
import dev.twiceb.passwordservice.dto.response.CategoryListResponse;
import dev.twiceb.passwordservice.dto.response.ExpiryPoliciesResponse;
import dev.twiceb.passwordservice.dto.response.PasswordVaultHealthResponse;
import dev.twiceb.passwordservice.repository.projection.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PasswordClientService {

    boolean isPasswordVaultEmpty();

    HeaderResponse<CategoryListResponse> getCategories();

    HeaderResponse<ExpiryPoliciesResponse> getExpiryPolicies();

    PasswordVaultHealthResponse getPasswordVaultHealth();

    List<LocalDateTime> getKeychainCreationDate(String interval);

    List<PasswordComplexityMetricProjection> getPasswordComplexityMetrics();

    List<KeychainNotificationProjection> getExpiringKeychains();

    Page<PasswordUpdateStatProjection> getPolicyStatistics(TimePeriod timePeriod, Pageable pageable);
}
