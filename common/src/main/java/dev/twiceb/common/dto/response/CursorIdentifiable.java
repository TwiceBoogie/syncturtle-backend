package dev.twiceb.common.dto.response;

import java.time.Instant;
import java.util.UUID;

public interface CursorIdentifiable {
    UUID getId();

    Instant getCreatedAt();
}
