package dev.twiceb.passwordservice.repository.projection;

import dev.twiceb.passwordservice.model.User;

import java.time.LocalDate;
import java.util.UUID;

public interface KeychainNotificationProjection {
    UUID getId();
    User getAccount();
    default UUID getUserId() { return getAccount().getId();}
    String getDomain();
    LocalDate getExpiryDate();
}
