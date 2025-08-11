package dev.twiceb.instanceservice.mapper;

import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.instanceservice.dto.response.ConfigDataResponse;
import dev.twiceb.instanceservice.dto.response.InstanceInfoResponse;
import dev.twiceb.instanceservice.dto.response.InstanceSetupResponse;
import dev.twiceb.instanceservice.service.InstanceService;
import lombok.RequiredArgsConstructor;

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
}
