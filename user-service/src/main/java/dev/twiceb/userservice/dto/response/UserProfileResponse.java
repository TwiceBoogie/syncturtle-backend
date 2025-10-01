package dev.twiceb.userservice.dto.response;

import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class UserProfileResponse {
    private UUID id;
    private boolean isOnboarded;
    private JsonNode onboardingSteps;
    private String billingAddressCountry;
    private String hasBillingAddress;
    @JsonProperty("lastWorkspaceId")
    private UUID lastTenantId;
    private String companyName;
    private String role;
    @JsonProperty("user")
    private UUID userId;
}
