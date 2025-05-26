package dev.twiceb.taskservice.service;

import java.util.Map;

import dev.twiceb.taskservice.dto.request.WorkspaceCheckRequest;
import dev.twiceb.taskservice.repository.projection.WorkspaceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import dev.twiceb.taskservice.dto.request.NewWorkspaceRequest;

public interface WorkspaceService {
    Map<String, Boolean> isWorkspaceExist(WorkspaceCheckRequest request, BindingResult bindingResult);
    Map<String, String> createWorkspace(NewWorkspaceRequest request, BindingResult bindingResult);
    Page<WorkspaceProjection> getWorkspaces(Pageable pageable);
}
