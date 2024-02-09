package dev.twiceb.passwordservice.repository.projection;

import dev.twiceb.passwordservice.enums.DomainStatus;
import dev.twiceb.passwordservice.model.Accounts;

import java.time.LocalDate;
import java.util.Date;

public interface KeychainNotificationProjection {
    Long getId();
    Accounts getAccount();
    default Long getUserId() { return this.getAccount().getUserId();}
    String getDomain();
    LocalDate getExpiryDate();
}
