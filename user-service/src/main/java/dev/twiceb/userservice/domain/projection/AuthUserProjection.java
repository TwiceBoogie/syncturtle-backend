package dev.twiceb.userservice.domain.projection;

import java.util.UUID;

public interface AuthUserProjection {
    UUID getId();

    String getEmail();

    String getFirstName();

    String getLastName();
}
