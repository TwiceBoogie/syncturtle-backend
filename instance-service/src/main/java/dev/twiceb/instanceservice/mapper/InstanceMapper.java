package dev.twiceb.instanceservice.mapper;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import dev.twiceb.common.application.internal.bundle.IssuedTokens;
import dev.twiceb.common.dto.request.AdminSignupRequest;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.instanceservice.dto.request.InstanceConfigurationUpdateRequest;
import dev.twiceb.instanceservice.dto.response.ConfigDataResponse;
import dev.twiceb.instanceservice.dto.response.InstanceAdminResponse;
import dev.twiceb.instanceservice.dto.response.InstanceInfoResponse;
import dev.twiceb.instanceservice.dto.response.InstanceSetupResponse;
import dev.twiceb.instanceservice.service.InstanceService;
import dev.twiceb.instanceservice.service.impl.InstanceServiceImpl.ConfigResult;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class InstanceMapper {

    private final InstanceService instanceService;
    private final BasicMapper mapper;

    public InstanceSetupResponse getInstanceInfo() {
        InstanceInfoResponse instance = mapper.convertToResponse(instanceService.getInstanceInfo(),
                InstanceInfoResponse.class);

        ConfigResult config = instanceService.getConfigurationValues();
        InstanceSetupResponse response = new InstanceSetupResponse();
        response.setConfig(ConfigDataResponse.fromConfigMap(config.getConfigKeys(),
                config.isSmtpConfigured(), config.getAdminBaseUrl(), config.getAppBaseUrl()));
        response.setInstance(instance);
        return response;
    }

    public Map<InstanceConfigurationKey, String> updateConfigurations(
            InstanceConfigurationUpdateRequest request) {
        return instanceService.updateConfigurations(request);
    }

    public IssuedTokens adminSignup(AdminSignupRequest request) {
        return instanceService.adminSignup(request);
    }

    public List<InstanceAdminResponse> getInstanceAdmins() {
        return mapper.convertToResponseList(instanceService.getInstanceAdmins(),
                InstanceAdminResponse.class);
    }
}
