package dev.twiceb.userservice.repository.projection;

public interface UserPrincipalProjection {
    Long getId();
    String getEmail();
    boolean isActive();
    String getRole();

    default String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    String getFirstName();
    String getLastName();
}
