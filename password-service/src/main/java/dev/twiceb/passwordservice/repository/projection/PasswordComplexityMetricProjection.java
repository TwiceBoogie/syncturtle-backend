package dev.twiceb.passwordservice.repository.projection;

import java.sql.Timestamp;

public interface PasswordComplexityMetricProjection {
    Long getId();
    int getPasswordLength();
    int character_types_used();
    double getEntropy();
    Timestamp getCheckDate();
}
