package dev.twiceb.instanceservice.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.Data;

@Data
public class InstanceAdminResponse {
    private UUID id;
    private UUID instance;
    private UserLiteViewResponse user;
    private int role;
    private Instant updatedAt;
    private Instant createdAt;
    private UUID updatedBy;
    private UUID createdBy;
}
