package dev.twiceb.common.event;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlanEvent {
    public enum Type {
        PLAN_CREATED, PLAN_UPDATED, PLAN_SOFT_DELETED
    }

    private Type type;
    private UUID id;
    private long version;
    private Instant updatedAt;
    private int schemaVersion;
}
