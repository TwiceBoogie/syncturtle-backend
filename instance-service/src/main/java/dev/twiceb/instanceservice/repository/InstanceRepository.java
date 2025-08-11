package dev.twiceb.instanceservice.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.instanceservice.model.Instance;
import dev.twiceb.instanceservice.repository.projection.InstanceProjection;

public interface InstanceRepository extends JpaRepository<Instance, UUID> {
    Optional<Instance> findFirstByOrderByCreatedAtAsc();

    Optional<InstanceProjection> findProjectedFirstByOrderByCreatedAtAsc();
}
