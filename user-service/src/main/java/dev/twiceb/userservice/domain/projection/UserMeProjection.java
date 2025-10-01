package dev.twiceb.userservice.domain.projection;

import java.util.UUID;

public interface UserMeProjection {
    UUID getId();

    String getUsername();

    String getEmail();

    String getFirstName();

    String getLastName();

    String getMobilePhone();

    String getDisplayName();

    String getLastLoginMedium();

    boolean isPasswordAutoSet();

    boolean isEmailVerified();

    boolean isActive();
}
