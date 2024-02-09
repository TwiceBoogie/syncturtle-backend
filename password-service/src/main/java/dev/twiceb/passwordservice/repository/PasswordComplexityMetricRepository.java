package dev.twiceb.passwordservice.repository;

import dev.twiceb.passwordservice.model.PasswordComplexityMetric;
import dev.twiceb.passwordservice.repository.projection.PasswordComplexityMetricProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordComplexityMetricRepository extends JpaRepository<PasswordComplexityMetric, Long> {

    @Query("SELECT pcm FROM PasswordComplexityMetric pcm")
    List<PasswordComplexityMetricProjection> findAllPCM();
}
