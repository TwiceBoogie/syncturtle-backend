package dev.twiceb.passwordservice.repository.projection;

import java.util.Date;
import java.util.List;

import dev.twiceb.passwordservice.enums.DomainStatus;

public interface UserPasswordProjection {
    Long getId();

    List<KeychainProjection> getKeychains();
}