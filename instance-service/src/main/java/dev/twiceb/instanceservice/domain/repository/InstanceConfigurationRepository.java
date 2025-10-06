package dev.twiceb.instanceservice.domain.repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.instanceservice.domain.model.InstanceConfiguration;

public interface InstanceConfigurationRepository
        extends JpaRepository<InstanceConfiguration, UUID> {
    List<InstanceConfiguration> findByKeyIn(Collection<InstanceConfigurationKey> keys);

    boolean existsByKey(InstanceConfigurationKey key);

    Optional<InstanceConfiguration> findByKey(InstanceConfigurationKey key);

    @Modifying
    @Query(value = """
            INSERT INTO instance_configurations (key, value, category, is_encrypted, created_at, updated_at)
            VALUES (:key, :value, :category, :enc, :now, :now)
            ON CONFLICT (key) DO NOTHING
            """,
            nativeQuery = true)
    int upsert(@Param("key") String key, @Param("value") String value,
            @Param("category") String category, @Param("enc") boolean encrypted,
            @Param("now") Instant now);
}
