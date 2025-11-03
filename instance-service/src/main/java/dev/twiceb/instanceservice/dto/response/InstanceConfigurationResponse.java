package dev.twiceb.instanceservice.dto.response;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InstanceConfigurationResponse {
    UUID id;
    Instant createdAt;
    Instant updatedAt;
    InstanceConfigurationKey key;
    String value;
    UUID createdBy;
    UUID updatedBy;
}
