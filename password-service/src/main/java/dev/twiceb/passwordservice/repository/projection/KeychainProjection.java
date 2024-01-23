package dev.twiceb.passwordservice.repository.projection;

import java.util.Date;

import dev.twiceb.passwordservice.enums.DomainStatus;
import dev.twiceb.passwordservice.model.UserPasswordExpirySetting;

public interface KeychainProjection {
    Long getId();
    String getUsername();
    String getDomain();
    String getFakePassword();
    UserPasswordExpirySetting getUserPasswordExpirySetting();
    default Date getDate() {
        return this.getUserPasswordExpirySetting().getExpiryDate();
    }
    DomainStatus getStatus();
}
