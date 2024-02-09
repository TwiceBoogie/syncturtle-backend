package dev.twiceb.passwordservice.repository.projection;

import dev.twiceb.common.enums.TimePeriod;

import java.time.Duration;
import java.time.LocalDateTime;

public interface PasswordUpdateStatProjection {
    Long getId();
    Long policyId();
    Duration getAvgUpdateInterval();
    TimePeriod getIntervalType();
    LocalDateTime getCreatedAt();
}
