package dev.twiceb.passwordservice.repository.projection;

import dev.twiceb.passwordservice.model.RotationPolicy;

import java.time.LocalDate;
import java.util.UUID;

public interface KeychainExpiringProjection {
    UUID getId();
    RotationPolicy getPolicy();
    LocalDate getExpiryDate();
}
