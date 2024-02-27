package dev.twiceb.passwordservice.repository.projection;

import dev.twiceb.passwordservice.model.User;

import java.time.LocalDate;

public interface KeychainNotificationProjection {
    Long getId();
    User getAccount();
    default Long getUserId() { return getAccount().getId();}
    String getDomain();
    LocalDate getExpiryDate();
}
