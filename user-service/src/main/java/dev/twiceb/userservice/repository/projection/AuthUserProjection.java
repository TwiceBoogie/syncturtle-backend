package dev.twiceb.userservice.repository.projection;

import dev.twiceb.common.enums.UserStatus;

import java.util.UUID;

public interface AuthUserProjection {
    UUID getId();
    String getEmail();
    String getFirstName();
    String getLastName();
}
