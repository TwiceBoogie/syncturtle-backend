package dev.twiceb.instanceservice.controller.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.twiceb.common.dto.response.ConfigResponse;
import dev.twiceb.instanceservice.service.InstanceService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/instance")
@RequiredArgsConstructor
public class InstanceControllerApi {

    private final InstanceService instanceService;

    @GetMapping("/config")
    public ConfigResponse getConfig() {
        return new ConfigResponse(instanceService.getConfigurationValues(),
                instanceService.getInstanceVersion());
    }

    @GetMapping("/config-version")
    public long getVersion() {
        return instanceService.getInstanceVersion();
    }
}
