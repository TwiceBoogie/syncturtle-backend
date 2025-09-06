package dev.twiceb.instanceservice.domain.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import dev.twiceb.instanceservice.domain.model.Instance;


public interface InstanceRepository extends JpaRepository<Instance, UUID> {
    <T> Optional<T> findFirstByOrderByCreatedAtAsc(Class<T> type);

    Optional<Long> findFirstConfigVersionByOrderByCreatedAtAsc();

    Optional<UUID> findFirstIdByOrderByCreatedAtAsc();

    // config versioning for cache invalidation
    @Modifying
    @Query("""
            UPDATE Instance i
            SET i.configVersion = i.configVersion + 1,
                i.configLastCheckedAt = :now
            WHERE i.id = :id and i.configVersion = :expected
            """)
    int bumpConfigVersion(@Param("id") UUID id, @Param("expected") long expected,
            @Param("now") Instant now);

    // app version check/update
    @Modifying
    @Query("""
            UPDATE Instance i
            SET i.latestVersion = :latest,
                i.lastCheckedAt = :now
            WHERE i.id = :id
            """)
    int recordAppVersionCheck(@Param("id") UUID id, @Param("latest") String latest,
            @Param("now") Instant now);

    @Modifying
    @Query("""
            UPDATE Instance i
            SET i.currentVersion = :current,
                i.lastCheckedAt = :now
            WHERE i.id = :id
            """)
    int recordAppUpgrade(@Param("id") UUID id, @Param("current") long current,
            @Param("now") Instant now);

    @Query("select i.configVersion from Instance i where i.id = :id")
    Optional<Long> findConfigVersionById(UUID id);
}
