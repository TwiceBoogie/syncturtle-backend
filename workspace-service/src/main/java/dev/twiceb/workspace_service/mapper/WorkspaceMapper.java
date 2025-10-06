package dev.twiceb.workspace_service.mapper;

import java.util.List;
import org.springframework.stereotype.Component;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.workspace_service.dto.request.CreateWorkspaceRequest;
import dev.twiceb.workspace_service.dto.response.WorkspaceMeResponse;
import dev.twiceb.workspace_service.service.WorkspaceService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WorkspaceMapper {

    private final WorkspaceService workspaceService;
    private final BasicMapper mapper;

    public List<WorkspaceMeResponse> createAdminWorkspace(CreateWorkspaceRequest request) {
        return mapper.convertToResponseList(workspaceService.createWorkspaceWithOwner(request),
                WorkspaceMeResponse.class);
    }

}
