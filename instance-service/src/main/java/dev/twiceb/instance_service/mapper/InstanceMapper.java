package dev.twiceb.instance_service.mapper;

import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.instance_service.dto.response.ConfigDataResponse;
import dev.twiceb.instance_service.dto.response.InstanceInfoResponse;
import dev.twiceb.instance_service.dto.response.InstanceSetupResponse;
import dev.twiceb.instance_service.service.InstanceService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InstanceMapper {

    private final InstanceService instanceService;
    private final BasicMapper mapper;

    public InstanceSetupResponse getInstanceInfo() {
        InstanceInfoResponse instance = mapper.convertToResponse(
                instanceService.getInstancePrinciple(), InstanceInfoResponse.class);
        InstanceSetupResponse response = new InstanceSetupResponse(true, instance.isSetupDone());
        response.setConfig(
                ConfigDataResponse.fromConfigMap(instanceService.getConfigurationValues()));
        response.setInstance(instance);
        return response;
    }
}
