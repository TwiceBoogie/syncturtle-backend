package dev.twiceb.passwordservice.repository.projection;

import java.util.UUID;

public interface BaseKeychainProjection {
    UUID getId();
    String getUsername();
    String getDomain();
}
