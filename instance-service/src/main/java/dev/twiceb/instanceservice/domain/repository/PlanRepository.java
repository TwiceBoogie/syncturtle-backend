package dev.twiceb.instanceservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import dev.twiceb.instanceservice.domain.model.Plan;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
    Optional<Plan> findByKey(String key);

    @Query(value = """
            SELECT * FROM plans
            WHERE features -> 'features' ->> 'aiAssist' = 'true'
            """, nativeQuery = true)
    List<Plan> findAllWithAiAssist();
}
