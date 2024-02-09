package dev.twiceb.passwordservice.repository.projection;

import java.time.LocalDate;

import dev.twiceb.passwordservice.enums.DomainStatus;

public interface KeychainProjection {
    Long getId();
    String getUsername();
    String getDomain();
    String getFakePassword();
    LocalDate getExpiryDate();
    DomainStatus getStatus();
}
