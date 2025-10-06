package dev.twiceb.workspace_service.service;

import java.util.List;
import java.util.UUID;
import dev.twiceb.workspace_service.dto.request.CreateWorkspaceRequest;
import dev.twiceb.workspace_service.repository.projections.WorkspacesProjection;

public interface WorkspaceService {
    List<WorkspacesProjection> createWorkspaceWithOwner(CreateWorkspaceRequest request);

    void softDeleteWorkspace(UUID id);
}
