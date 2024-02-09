package dev.twiceb.passwordservice.repository.projection;

import dev.twiceb.passwordservice.enums.DomainStatus;
import dev.twiceb.passwordservice.model.PasswordExpiryPolicy;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface KeychainExpiringProjection {
    Long getId();
    PasswordExpiryPolicy getPolicy();
    LocalDate getExpiryDate();
}
