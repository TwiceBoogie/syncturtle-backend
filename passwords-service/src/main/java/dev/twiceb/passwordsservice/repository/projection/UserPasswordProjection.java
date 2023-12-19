package dev.twiceb.passwordsservice.repository.projection;

import java.util.Date;

import dev.twiceb.passwordsservice.enums.DomainStatus;

public interface UserPasswordProjection {
    Long getId();

    String getDomain();

    String getFakePassword();

    Date getDate();

    DomainStatus getStatus();
}