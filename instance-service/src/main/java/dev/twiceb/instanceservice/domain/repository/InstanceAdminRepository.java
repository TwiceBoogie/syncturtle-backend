package dev.twiceb.instanceservice.domain.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.instanceservice.domain.model.InstanceAdmin;

public interface InstanceAdminRepository extends JpaRepository<InstanceAdmin, UUID> {
    Optional<UUID> findFirstIdByOrderByCreatedAtAsc();
}
