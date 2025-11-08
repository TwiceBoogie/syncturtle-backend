package dev.twiceb.instanceservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.twiceb.common.repository.projections.OnlyId;
import dev.twiceb.instanceservice.domain.model.InstanceAdmin;
import dev.twiceb.instanceservice.domain.projection.InstanceAdminProjection;

public interface InstanceAdminRepository extends JpaRepository<InstanceAdmin, UUID> {
    Optional<OnlyId> findFirstIdByOrderByCreatedAtAsc();

    @Query("""
            SELECT
                ia.id AS id,
                ia.instance.id AS instance,
                u AS user,
                ia.role AS role,
                ia.updatedAt AS updatedAt,
                ia.createdAt AS createdAt
            FROM InstanceAdmin ia
            JOIN UserLite u ON u.id = ia.userId
            WHERE ia.instance.id = :instanceId AND ia.deletedAt IS NULL
            ORDER BY ia.createdAt DESC
            """)
    List<InstanceAdminProjection> findAllByInstanceId(UUID instanceId);

    boolean existsByInstanceIdAndUserIdAndRoleGreaterThanEqual(UUID instanceId, UUID userId,
            int role);
}
