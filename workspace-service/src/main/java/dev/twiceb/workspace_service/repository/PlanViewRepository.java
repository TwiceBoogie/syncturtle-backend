package dev.twiceb.workspace_service.repository;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import dev.twiceb.workspace_service.model.PlanView;

public interface PlanViewRepository extends JpaRepository<PlanView, UUID> {
    @Modifying
    @Query("""
            UPDATE PlanView pv
                SET pv.version = :version,
                    pv.updatedAt = :now
                WHERE iv.id = :id
                AND iv.version > :version
            """)
    int upsertActiveIfNewer(@Param("id") UUID id, @Param("version") long version,
            @Param("now") Instant now);
}
