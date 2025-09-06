package dev.twiceb.instanceservice.mapper;

import java.util.Map;
import org.springframework.stereotype.Component;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.dto.response.TokenGrant;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.instanceservice.dto.request.InstanceConfigurationUpdateRequest;
import dev.twiceb.instanceservice.dto.response.ConfigDataResponse;
import dev.twiceb.instanceservice.dto.response.InstanceInfoResponse;
import dev.twiceb.instanceservice.dto.response.InstanceSetupResponse;
import dev.twiceb.instanceservice.service.InstanceService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InstanceMapper {

    private final InstanceService instanceService;
    private final BasicMapper mapper;

    public InstanceSetupResponse getInstanceInfo() {
        InstanceInfoResponse instance = mapper.convertToResponse(
                instanceService.getInstancePrinciple(), InstanceInfoResponse.class);
        if (instance == null)
            return new InstanceSetupResponse(false, false);
        InstanceSetupResponse response = new InstanceSetupResponse(true, instance.isSetupDone());
        response.setConfig(
                ConfigDataResponse.fromConfigMap(instanceService.getConfigurationValues()));
        response.setInstance(instance);
        return response;
    }

    public Map<InstanceConfigurationKey, String> updateConfigurations(
            InstanceConfigurationUpdateRequest request) {
        return instanceService.updateConfigurations(request);
    }

    public TokenGrant adminSignup(AdminSignupRequest request) {
        return instanceService.adminSignup(request);
    }
}
