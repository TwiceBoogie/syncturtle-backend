package dev.twiceb.workspace_service.service.impl;

import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.common.dto.context.AuthContext;
import dev.twiceb.workspace_service.dto.request.CreateWorkspaceRequest;
import dev.twiceb.workspace_service.model.Workspace;
import dev.twiceb.workspace_service.repository.WorkspaceRepository;
import dev.twiceb.workspace_service.service.WorkspaceService;
import dev.twiceb.workspace_service.utils.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;

    @Override
    @Transactional
    public void createWorkspaceWithOwner(CreateWorkspaceRequest request) {
        ValidationUtils.validateName(request.getName());
        ValidationUtils.validateSlug(request.getSlug());
        // 1; create workspace;
        Workspace ws = new Workspace();
        ws.setName(request.getName());
        ws.setSlug(request.getSlug());
        ws.setOrganizationSize(request.getOrganizationSize());
        ws.setOwnerId(AuthContext.get());

    }

    @Override
    @Transactional
    public void softDeleteWorkspace(UUID id) {
        // 1; load the entity (still visible due to @Where; use a custom finder that bypasses @Where
        // if needed)
        Workspace ws = workspaceRepository.findByIdIncludingDeleted(id).orElseThrow();
        if (ws.getDeletedAt() != null)
            return; // already deleted

        // 2; rename slug first to free it
        String original = ws.getSlug();
        String newSlug = original + "__" + Instant.now().getEpochSecond();
        ws.setSlug(newSlug);
        workspaceRepository.saveAndFlush(ws);

        // 3; perform soft delete (sets deleted_at via @SQLDelete)
        workspaceRepository.deleteById(ws.getId());
    }
}
