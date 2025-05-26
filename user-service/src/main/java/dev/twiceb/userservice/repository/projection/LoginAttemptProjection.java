package dev.twiceb.userservice.repository.projection;

import java.util.UUID;

public interface LoginAttemptProjection {
    UUID getId();
    String getIpAddress();
}
