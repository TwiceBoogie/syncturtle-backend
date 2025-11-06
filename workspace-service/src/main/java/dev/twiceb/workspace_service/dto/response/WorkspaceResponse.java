package dev.twiceb.workspace_service.dto.response;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.dto.response.CursorIdentifiable;
import lombok.Data;

@Data
public class WorkspaceResponse implements CursorIdentifiable {
    private UUID id;
    private UUID owner;
    private String name;
    private String slug;
    private int organizationSize;
    private int totalMembers;
    private Instant createdAt;
    private Instant updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}
