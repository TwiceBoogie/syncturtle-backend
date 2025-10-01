package dev.twiceb.workspace_service.controller.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.workspace_service.service.impl.WorkspaceBasePermission;
import dev.twiceb.workspace_service.utils.PermissionClasses;

@RestController
@RequestMapping("/workspaces")
@PermissionClasses({WorkspaceBasePermission.class})
public class WorkspaceController {

}
