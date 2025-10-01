package dev.twiceb.common.event;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.enums.InstanceEdition;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InstanceEvent {
    public enum Type {
        INSTANCE_CREATED, INSTANCE_UPDATED, INSTANCE_SOFT_DELETED
    }

    private Type type;
    private UUID id; // aggregate id (partition key)
    private String slug;
    private InstanceEdition edition;
    private long version;
    private Instant updatedAt;
    private int schemaVersion; // 1 + bump (if payload shape change)
}
