package dev.twiceb.instance_service.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.instance_service.model.InstanceConfiguration;

public interface InstanceConfigurationRepository
        extends JpaRepository<InstanceConfiguration, UUID> {
}
