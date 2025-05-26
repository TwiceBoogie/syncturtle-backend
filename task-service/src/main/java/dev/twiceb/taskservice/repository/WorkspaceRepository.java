package dev.twiceb.taskservice.repository;

import java.util.UUID;

import dev.twiceb.taskservice.repository.projection.WorkspaceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dev.twiceb.taskservice.model.Workspace;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {

    @Query("SELECT ws FROM Workspace ws WHERE ws.owner.id = :userId")
    Page<WorkspaceProjection> getWorkspaceNames(@Param("userId") UUID userId, Pageable pageable);

    boolean existsBySlug(String slug);
}
