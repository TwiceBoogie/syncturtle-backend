package dev.twiceb.instanceservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.instanceservice.model.InstanceConfiguration;

public interface InstanceConfigurationRepository
        extends JpaRepository<InstanceConfiguration, UUID> {
}
