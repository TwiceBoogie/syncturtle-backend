package dev.twiceb.userservice.repository.projection;

import dev.twiceb.common.enums.UserStatus;

public interface AuthUserProjection {
    Long getId();
    String getEmail();
    String getPassword();
    String getRole();
    boolean isVerified();
    UserStatus getUserStatus();
}
