package dev.twiceb.passwordservice.service;

import dev.twiceb.passwordservice.repository.projection.PasswordComplexityMetricProjection;

import java.sql.Timestamp;
import java.util.List;

public interface PasswordClientService {

    List<Timestamp> getKeychainCreationTimestamps(String interval);

    List<PasswordComplexityMetricProjection> getPasswordComplexityMetrics();

}
