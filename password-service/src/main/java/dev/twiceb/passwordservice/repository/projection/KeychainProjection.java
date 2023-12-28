package dev.twiceb.passwordservice.repository.projection;

import dev.twiceb.passwordservice.enums.DomainStatus;

public interface KeychainProjection {
    Long getId();

    String getDomain();

    String getFakePassword();

    DomainStatus geStatus();
}
