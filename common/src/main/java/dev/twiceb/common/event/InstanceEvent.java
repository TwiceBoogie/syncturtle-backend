package dev.twiceb.common.event;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.enums.InstanceEdition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstanceEvent {
    public enum Type {
        INSTANCE_CREATED, INSTANCE_UPDATED, INSTANCE_SOFT_DELETED
    }

    private Type type;
    private UUID id; // uuid of instances.id
    private InstanceEdition edition;
    private String slug;
    private long version;
    private String machineSignature; // <hash or id>; rename instance_id?
    private String apiBaseUrl;
    private String vmHost;
    private Instant occurredAt;
    private int schemaVersion; // 1 + bump (if payload shape change)
    private boolean test;
}
