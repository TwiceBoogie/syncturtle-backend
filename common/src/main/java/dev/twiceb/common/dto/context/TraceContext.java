package dev.twiceb.common.dto.context;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TraceContext {
    private String correlationId;
    private String idempotencyKey; // can be the same as correlationId;
    private String producer; // "instance-service"
    private String schemaVersion; // v1
}
