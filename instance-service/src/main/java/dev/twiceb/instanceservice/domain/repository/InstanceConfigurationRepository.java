package dev.twiceb.instanceservice.domain.repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.instanceservice.domain.model.InstanceConfiguration;

public interface InstanceConfigurationRepository
        extends JpaRepository<InstanceConfiguration, UUID> {
    List<InstanceConfiguration> findByKeyIn(Collection<InstanceConfigurationKey> keys);
}
