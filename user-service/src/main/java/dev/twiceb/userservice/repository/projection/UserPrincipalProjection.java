package dev.twiceb.userservice.repository.projection;

import dev.twiceb.common.enums.UserRole;

public interface UserPrincipalProjection {
    Long getId();
    String getUsername();
    boolean isVerified();
    UserRole getRole();
    String getUserStatus();

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    String getFirstName();
    String getLastName();
}
