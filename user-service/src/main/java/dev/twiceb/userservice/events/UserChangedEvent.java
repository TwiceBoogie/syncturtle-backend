package dev.twiceb.userservice.events;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.event.UserEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserChangedEvent {
    private final UserEvent.Type type;
    private final UUID userId;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String displayName;
    private final Instant dateJoined;
    private final Instant occurredAt;
}
