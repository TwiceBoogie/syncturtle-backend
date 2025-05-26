package dev.twiceb.userservice.repository.projection;

import java.util.List;
import java.util.UUID;

import dev.twiceb.common.enums.UserRole;

public interface UserPrincipalProjection {
    UUID getId();

    String getUsername();

    boolean isVerified();

    UserRole getRole();

    String getUserStatus();

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    List<DevicePrincipleProjection> getUserDevices();

    String getFirstName();

    String getLastName();
}
