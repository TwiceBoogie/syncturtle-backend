package dev.twiceb.passwordservice.repository.projection;

import java.util.List;

public interface UserPasswordProjection {
    Long getId();

    List<KeychainProjection> getKeychains();
}