package dev.twiceb.instanceservice.domain.projection;

import java.time.Instant;
import java.util.UUID;

public interface UserLiteViewProjection {
    UUID getId();

    String getEmail();

    String getFirstName();

    String getLastName();

    String getDisplayname();

    Instant getDateJoined();
}
