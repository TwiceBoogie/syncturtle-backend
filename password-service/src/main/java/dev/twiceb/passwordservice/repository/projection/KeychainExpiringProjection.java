package dev.twiceb.passwordservice.repository.projection;

import dev.twiceb.passwordservice.model.RotationPolicy;

import java.time.LocalDate;

public interface KeychainExpiringProjection {
    Long getId();
    RotationPolicy getPolicy();
    LocalDate getExpiryDate();
}
