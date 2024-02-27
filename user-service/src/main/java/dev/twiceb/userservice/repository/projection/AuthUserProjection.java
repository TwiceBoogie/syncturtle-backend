package dev.twiceb.userservice.repository.projection;

import dev.twiceb.common.enums.UserStatus;

public interface AuthUserProjection {
    Long getId();
    String getEmail();
    String getFirstName();
    String getLastName();
}
