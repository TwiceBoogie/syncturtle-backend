package dev.twiceb.taskservice.service.impl;

import java.util.Map;
import java.util.UUID;

import dev.twiceb.taskservice.dto.request.WorkspaceCheckRequest;
import dev.twiceb.taskservice.repository.projection.WorkspaceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import dev.twiceb.common.util.AuthUtil;
import dev.twiceb.taskservice.dto.request.NewWorkspaceRequest;
import dev.twiceb.taskservice.model.User;
import dev.twiceb.taskservice.model.Workspace;
import dev.twiceb.taskservice.repository.WorkspaceRepository;
import dev.twiceb.taskservice.service.UserService;
import dev.twiceb.taskservice.service.WorkspaceService;
import dev.twiceb.taskservice.service.util.TaskServiceHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkspaceServiceImpl implements WorkspaceService {

    private WorkspaceRepository workspaceRepository;
    private TaskServiceHelper taskServiceHelper;
    private UserService userService;

    @Override
    public Map<String, Boolean> isWorkspaceExist(WorkspaceCheckRequest request, BindingResult bindingResult) {
        taskServiceHelper.processBindingResults(bindingResult);
        return Map.of("message", workspaceRepository.existsBySlug(request.getSlug()));
    }

    @Override
    public Map<String, String> createWorkspace(NewWorkspaceRequest request, BindingResult bindingResult) {
        taskServiceHelper.processBindingResults(bindingResult);
        User authUser = userService.getAuthUser();
        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setLogo(request.getLogo());
        workspace.setSlug(request.getSlug());
        workspace.setOwner(authUser);
        workspaceRepository.save(workspace);
        return Map.of("message", "Workspace created");
    }

    @Override
    public Page<WorkspaceProjection> getWorkspaces(Pageable pageable) {
        UUID authUserId = AuthUtil.getAuthenticatedUserId();
        return workspaceRepository.getWorkspaceNames(authUserId, pageable);
    }

}
