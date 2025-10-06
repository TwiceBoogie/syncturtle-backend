package dev.twiceb.workspace_service.dto.response;

import java.util.UUID;
import lombok.Data;

@Data
public class WorkspaceMeResponse {
    private UUID id;
    private String name;
    private String slug;
    private UUID ownerId;
    private Integer role;
    private Integer totalMembers;
}
