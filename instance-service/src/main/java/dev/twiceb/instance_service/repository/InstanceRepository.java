package dev.twiceb.instance_service.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.instance_service.model.Instance;
import dev.twiceb.instance_service.repository.projection.InstanceProjection;

public interface InstanceRepository extends JpaRepository<Instance, UUID> {
    Optional<Instance> findFirstByOrderByCreatedAtAsc();

    Optional<InstanceProjection> findProjectedFirstByOrderByCreatedAtAsc();
}
