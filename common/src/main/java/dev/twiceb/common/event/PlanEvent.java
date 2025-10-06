package dev.twiceb.common.event;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanEvent {
    public enum Type {
        PLAN_CREATED, PLAN_UPDATED, PLAN_SOFT_DELETED
    }

    private Type type;
    private UUID id;
    private String key;
    private long version;
    private Instant updatedAt;
    private int schemaVersion;
}
