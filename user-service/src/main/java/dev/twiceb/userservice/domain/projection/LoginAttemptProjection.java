package dev.twiceb.userservice.domain.projection;

import java.util.UUID;

public interface LoginAttemptProjection {
    UUID getId();

    String getIpAddress();
}
