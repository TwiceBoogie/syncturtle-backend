package dev.twiceb.passwordservice.dto.response;

import lombok.Data;

@Data
public class ExpiryPoliciesResponse {
    private Long id;
    private String policyName;
    private int maxExpiryDays;
    private int notificationDays;
}
