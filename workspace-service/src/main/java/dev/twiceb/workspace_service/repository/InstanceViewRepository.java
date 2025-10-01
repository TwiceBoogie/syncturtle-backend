package dev.twiceb.workspace_service.repository;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import dev.twiceb.common.enums.InstanceEdition;
import dev.twiceb.workspace_service.model.InstanceView;

public interface InstanceViewRepository extends JpaRepository<InstanceView, UUID> {

    @Modifying
    @Query("""
            UPDATE InstanceView iv
                SET iv.slug = :slug,
                    iv.edition = :edition,
                    iv.version = :version,
                    iv.updatedAt = :now
                WHERE iv.id = :id
                AND iv.version < :version
            """)
    int upsertActiveIfNewer(@Param("id") UUID id, @Param("slug") String slug,
            @Param("edition") InstanceEdition edition, @Param("version") long version,
            @Param("now") Instant now);

}
