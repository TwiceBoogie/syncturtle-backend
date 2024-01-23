package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.PasswordComplexityMetric;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordComplexityMetricRepository extends JpaRepository<PasswordComplexityMetric, Long> {
}
