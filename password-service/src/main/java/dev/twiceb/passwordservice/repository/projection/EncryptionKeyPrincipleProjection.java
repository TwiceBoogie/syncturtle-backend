package dev.twiceb.passwordservice.repository.projection;

import dev.twiceb.passwordservice.model.User;

public interface EncryptionKeyPrincipleProjection {
    Long getId();
    String getDek();
    User getUser();
    boolean isEnabled();
}
