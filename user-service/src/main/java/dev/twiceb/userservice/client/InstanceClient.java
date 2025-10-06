package dev.twiceb.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import dev.twiceb.common.config.FeignConfiguration;
import dev.twiceb.common.constants.FeignConstants;
import dev.twiceb.common.constants.PathConstants;
import dev.twiceb.common.dto.response.ConfigResponse;
import dev.twiceb.common.dto.response.InstanceStatusResult;

@FeignClient(value = FeignConstants.INSTANCE_SERVICE, path = PathConstants.INTERNAL_V1_INSTANCE,
        configuration = FeignConfiguration.class)
public interface InstanceClient {

    @GetMapping("/config")
    ConfigResponse getConfig();

    @GetMapping("/config-version")
    long getConfigVersion();

    @GetMapping("/status")
    InstanceStatusResult getStatus();
}
