package dev.twiceb.userservice.service.impl;

import java.util.Map;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import dev.twiceb.common.dto.response.ConfigResponse;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.userservice.client.InstanceClient;
import dev.twiceb.userservice.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeatureFlagServiceImpl implements FeatureFlagService {

    private final InstanceClient instanceClient;

    @Override
    public Map<InstanceConfigurationKey, String> getConfig() {
        long version = instanceClient.getConfigVersion(); // cheap probe
        return getConfigCached(version).getValues(); // cache aware
    }

    @Override
    public String get(InstanceConfigurationKey key) {
        return getConfig().getOrDefault(key, "0");
    }

    @Cacheable(value = "s2s:instance:configuration", keyGenerator = "versionKeyGen",
            unless = "#result == null || #result.values == null || #result.values.isEmpty()",
            sync = true)
    protected ConfigResponse getConfigCached(long version) {
        return instanceClient.getConfig();
    }

}
