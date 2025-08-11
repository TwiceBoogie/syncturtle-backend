package dev.twiceb.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import dev.twiceb.common.config.FeignConfiguration;
import dev.twiceb.common.dto.response.ConfigResponse;

@FeignClient(value = "instance-service", path = "/api/v1/instance",
        configuration = FeignConfiguration.class)
public interface InstanceClient {

    @GetMapping("/config")
    ConfigResponse getConfig();

    @GetMapping("/config-version")
    long getVersion();
}
