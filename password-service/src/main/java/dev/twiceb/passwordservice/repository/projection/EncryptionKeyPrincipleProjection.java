package dev.twiceb.passwordservice.repository.projection;

import java.time.LocalDateTime;

public interface EncryptionKeyPrincipleProjection {
    Long getId();

    String getName();

    String getDescription();

    String getAlgorithm();

    LocalDateTime getExpirationDate();

    boolean isEnabled();
}
