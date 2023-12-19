package dev.twiceb.userservice.repository.projection;

public interface AuthUserProjection {
    Long getId();

    String getEmail();

    String getFirstName();

    String getLastName();

    String getPassword();

    String getRole();

    boolean isActive();
}
