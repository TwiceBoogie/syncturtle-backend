package dev.twiceb.workspace_service.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import dev.twiceb.common.dto.response.CursorPageResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.workspace_service.dto.request.CreateWorkspaceRequest;

import dev.twiceb.workspace_service.dto.response.WorkspaceMeResponse;
import dev.twiceb.workspace_service.dto.response.WorkspaceResponse;
import dev.twiceb.workspace_service.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WorkspaceMapper {

    private final WorkspaceService workspaceService;
    private final BasicMapper mapper;

    public List<WorkspaceMeResponse> createAdminWorkspace(CreateWorkspaceRequest request) {
        return mapper.convertToResponseList(workspaceService.createWorkspaceWithOwner(request),
                WorkspaceMeResponse.class);
    }

    public CursorPageResponse<WorkspaceResponse> getAllWorkspaces(String cursor, int perPage,
            String search) {
        return mapper.convertToCursorPageResponse(
                workspaceService.getAllWorkspaces(cursor, perPage, search), perPage,
                WorkspaceResponse.class);
    }

}
