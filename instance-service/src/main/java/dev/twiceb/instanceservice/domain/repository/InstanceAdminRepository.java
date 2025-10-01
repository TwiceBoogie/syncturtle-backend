package dev.twiceb.instanceservice.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.instanceservice.domain.model.InstanceAdmin;
import dev.twiceb.instanceservice.domain.projection.InstanceAdminProjection;
import dev.twiceb.instanceservice.domain.projection.OnlyId;

public interface InstanceAdminRepository extends JpaRepository<InstanceAdmin, UUID> {
    Optional<OnlyId> findFirstIdByOrderByCreatedAtAsc();

    List<InstanceAdminProjection> findAllByInstance_Id(UUID instanceId);
}
