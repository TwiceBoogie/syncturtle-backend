package dev.twiceb.workspace_service.service;

import java.util.List;
import java.util.UUID;
import dev.twiceb.workspace_service.dto.request.CreateWorkspaceRequest;
import dev.twiceb.workspace_service.repository.projections.WorkspaceMeProjection;
import dev.twiceb.workspace_service.repository.projections.WorkspaceProjection;

public interface WorkspaceService {
    List<WorkspaceMeProjection> createWorkspaceWithOwner(CreateWorkspaceRequest request);

    void softDeleteWorkspace(UUID id);

    List<WorkspaceProjection> getAllWorkspaces(String cursor, int perPage, String search);
}
