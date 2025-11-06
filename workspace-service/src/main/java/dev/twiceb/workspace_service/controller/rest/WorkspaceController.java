package dev.twiceb.workspace_service.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.common.dto.response.CursorPageResponse;
import dev.twiceb.workspace_service.dto.response.WorkspaceResponse;
import dev.twiceb.workspace_service.mapper.WorkspaceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceMapper mapper;

    @GetMapping()
    public ResponseEntity<CursorPageResponse<WorkspaceResponse>> list(
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int perPage,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(mapper.getAllWorkspaces(cursor, perPage, search));
    }

}
