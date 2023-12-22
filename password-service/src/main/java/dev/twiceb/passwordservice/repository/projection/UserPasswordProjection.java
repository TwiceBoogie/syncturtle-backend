package dev.twiceb.passwordservice.repository.projection;

import java.util.Date;

import dev.twiceb.passwordservice.enums.DomainStatus;

public interface UserPasswordProjection {
    Long getId();

    String getDomain();

    String getFakePassword();

    Date getDate();

    DomainStatus getStatus();
}