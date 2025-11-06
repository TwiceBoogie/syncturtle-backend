package dev.twiceb.common.repository.projections;

import java.time.Instant;
import java.util.UUID;

public interface HasIdCreatedAt {
    UUID getId();

    Instant getCreatedAt();
}
