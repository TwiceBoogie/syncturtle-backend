package dev.twiceb.workspace_service.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import dev.twiceb.workspace_service.model.Workspace;
import dev.twiceb.workspace_service.repository.projections.WorkspacesProjection;
import feign.Param;
import jakarta.persistence.QueryHint;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
    Optional<Workspace> findBySlugIgnoreCase(String slug);

    boolean existsBySlugIgnoreCase(String slug);

    // JPA's delete path is signle sql statement from @SQLDelete
    @Query("SELECT w FROM Workspace w WHERE w.id = :id")
    @QueryHints({@QueryHint(name = "org.hibernate.annotations.QueryHints.PASS_DISTINCT_THROUGH",
            value = "false")})
    Optional<Workspace> findByIdIncludingDeleted(@Param("id") UUID id);

    @Query("""
            SELECT w.id AS id, w.name AS name, w.slug AS slug, w.ownerId AS ownerId,
            (SELECT CAST(m.role AS integer) FROM WorkspaceMember m
                WHERE m.workspace = w AND m.memberId = :userId AND m.active = true) AS role,
            (SELECT CAST(COUNT(m2) AS integer) FROM WorkspaceMember m2
                WHERE m2.workspace = w AND m2.active = true) AS totalMembers
            FROM Workspace w
            WHERE exists (
                SELECT 1 FROM WorkspaceMember me
                WHERE me.workspace = w AND me.memberId = :userId and me.active = true
            )
            AND (:search IS NULL OR LOWER(w.name) LIKE LOWER(CONCAT('%', :SEARCH, '%')))
            """)
    List<WorkspacesProjection> findMyWorkspaces(UUID userId, String search);
}
