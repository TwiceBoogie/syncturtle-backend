package dev.twiceb.instanceservice.service;

import java.util.Map;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.instanceservice.dto.request.InstanceConfigurationUpdateRequest;
import dev.twiceb.instanceservice.domain.projection.InstanceProjection;

public interface InstanceService {
    InstanceProjection getInstancePrinciple();

    Map<InstanceConfigurationKey, String> getConfigurationValues();

    long getInstanceVersion();

    Map<InstanceConfigurationKey, String> updateConfigurations(
            InstanceConfigurationUpdateRequest request);

    TokenGrant adminSignup(AdminSignupRequest payload);
}
