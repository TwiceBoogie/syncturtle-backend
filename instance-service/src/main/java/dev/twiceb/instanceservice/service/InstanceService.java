package dev.twiceb.instanceservice.service;

import java.util.List;
import java.util.Map;
import dev.twiceb.common.application.internal.bundle.IssuedTokens;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.response.InstanceStatusResult;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.instanceservice.dto.request.InstanceConfigurationUpdateRequest;
import dev.twiceb.instanceservice.service.impl.InstanceServiceImpl.ConfigResult;
import dev.twiceb.instanceservice.domain.projection.InstanceAdminProjection;
import dev.twiceb.instanceservice.domain.projection.InstanceProjection;

public interface InstanceService {
    InstanceProjection getInstanceInfo();

    ConfigResult getConfigurationValues();

    Long getConfigVersion();

    Map<InstanceConfigurationKey, String> updateConfigurations(
            InstanceConfigurationUpdateRequest request);

    IssuedTokens adminSignup(AdminSignupRequest payload);

    InstanceStatusResult getInstanceStatus();

    List<InstanceAdminProjection> getInstanceAdmins();
}
