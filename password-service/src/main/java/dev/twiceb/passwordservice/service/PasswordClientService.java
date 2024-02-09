package dev.twiceb.passwordservice.service;

import dev.twiceb.common.enums.TimePeriod;
import dev.twiceb.passwordservice.repository.projection.KeychainNotificationProjection;
import dev.twiceb.passwordservice.repository.projection.PasswordComplexityMetricProjection;
import dev.twiceb.passwordservice.repository.projection.PasswordUpdateStatProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface PasswordClientService {

    List<LocalDateTime> getKeychainCreationDate(String interval);

    List<PasswordComplexityMetricProjection> getPasswordComplexityMetrics();

    List<KeychainNotificationProjection> getExpiringKeychains();

    Page<PasswordUpdateStatProjection> getPolicyStatistics(TimePeriod timePeriod, Pageable pageable);

}
