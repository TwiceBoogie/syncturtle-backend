package dev.twiceb.userservice.events;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.enums.AuthMedium;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAuthenticatedEvent {
    private final UUID userId;
    private final boolean isSignup;
    private final AuthMedium medium;
    private final String email;
    private final Instant occurredAt;
}
