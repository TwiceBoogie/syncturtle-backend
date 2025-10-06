package dev.twiceb.instanceservice.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.common.dto.response.ConfigResponse;
import dev.twiceb.common.dto.response.InstanceStatusResult;
import dev.twiceb.instanceservice.service.InstanceService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/internal/v1/instances")
@RequiredArgsConstructor
public class InstanceControllerApi {

    private final InstanceService instanceService;

    @GetMapping("/config")
    public ConfigResponse getConfig() {
        return new ConfigResponse(instanceService.getConfigurationValues().getConfigKeys(),
                instanceService.getConfigVersion());
    }

    @GetMapping("/config-version")
    public Long getConfigVersion() {
        return instanceService.getConfigVersion();
    }

    @GetMapping("/status")
    public InstanceStatusResult getInstanceStatus() {
        return instanceService.getInstanceStatus();
    }
}
