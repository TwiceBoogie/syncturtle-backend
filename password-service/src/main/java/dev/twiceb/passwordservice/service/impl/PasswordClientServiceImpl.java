package dev.twiceb.passwordservice.service.impl;

import dev.twiceb.common.enums.TimePeriod;
import dev.twiceb.passwordservice.repository.KeychainRepository;
import dev.twiceb.passwordservice.repository.PasswordComplexityMetricRepository;
import dev.twiceb.passwordservice.repository.PasswordExpiryConfigRepository;
import dev.twiceb.passwordservice.repository.PasswordUpdateStatRepository;
import dev.twiceb.passwordservice.repository.projection.KeychainNotificationProjection;
import dev.twiceb.passwordservice.repository.projection.PasswordComplexityMetricProjection;
import dev.twiceb.passwordservice.repository.projection.PasswordUpdateStatProjection;
import dev.twiceb.passwordservice.service.PasswordClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PasswordClientServiceImpl implements PasswordClientService {

    private final KeychainRepository keychainRepository;
    private final PasswordExpiryConfigRepository passwordExpiryConfigRepository;
    private final PasswordComplexityMetricRepository passwordComplexityMetricRepository;
    private final PasswordUpdateStatRepository passwordUpdateStatRepository;

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
