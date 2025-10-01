package dev.twiceb.instanceservice.dto.response;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InstanceAdminResponse {
    private UUID id;
    @JsonProperty("instance")
    private UUID instanceId;
    @JsonProperty("user")
    private UUID userId;
    private int role;
}
