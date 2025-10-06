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
public class UserEvent {
    public enum Type {
        USER_CREATED, USER_UPDATED, USER_SOFT_DELETED
    }

    private String eventId;
    private Instant occurredAt;

    private Type type;
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private String displayName;
    private Instant dateJoined;
    private Long version;
}
