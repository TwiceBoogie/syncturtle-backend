package dev.twiceb.userservice.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import dev.twiceb.common.dto.response.InstanceStatusResult;
import dev.twiceb.userservice.client.InstanceClient;
import dev.twiceb.userservice.service.InstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstanceServiceImpl implements InstanceService {

    private final InstanceClient instanceClient;

    @Override
    public boolean isSetupDone() {
        return status().isSetupDone();
    }

    @Override
    @Cacheable(cacheNames = "s2s:instance:status", key = "'singleton'", sync = true)
    public InstanceStatusResult status() {
        log.info("===> Should hit once and then cached <===");
        return instanceClient.getStatus();
    }

}
