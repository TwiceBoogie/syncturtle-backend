package dev.twiceb.userservice.domain.projection;

import java.util.UUID;
import com.fasterxml.jackson.databind.JsonNode;

public interface ProfileProjection {
    UUID getId();

    boolean isOnboarded();

    JsonNode getOnboardingStep();

    String getBillingAddressCountry();

    boolean getHasBillingAddress();

    UUID getLastTenantId();

    String getCompanyName();

    String getRole();

    UserIdProjection getUser();

    default UUID getUserId() {
        return getUser().getId();
    }
}
