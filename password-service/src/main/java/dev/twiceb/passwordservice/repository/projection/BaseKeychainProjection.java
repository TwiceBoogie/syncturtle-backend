package dev.twiceb.passwordservice.repository.projection;

public interface BaseKeychainProjection {
    Long getId();
    String getUsername();
    String getDomain();
}
