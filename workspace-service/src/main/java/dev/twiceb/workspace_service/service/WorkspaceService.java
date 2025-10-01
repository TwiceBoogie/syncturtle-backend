package dev.twiceb.workspace_service.service;

import java.util.UUID;
import dev.twiceb.workspace_service.dto.request.CreateWorkspaceRequest;

public interface WorkspaceService {
    void createWorkspaceWithOwner(CreateWorkspaceRequest request);

    void softDeleteWorkspace(UUID id);
}
