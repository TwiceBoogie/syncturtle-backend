package dev.twiceb.passwordservice.repository.projection;

public interface ExpiryPolicyProjection {
    Long getId();
    String getPolicyName();
    int getMaxExpiryDays();
    int getNotificationDays();
}
