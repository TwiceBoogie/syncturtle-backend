package dev.twiceb.userservice.service.impl;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import dev.twiceb.userservice.client.InstanceClient;
import dev.twiceb.userservice.service.FeatureFlagService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeatureFlagServiceImpl implements FeatureFlagService {

    private final InstanceClient instanceClient;
    private final AtomicLong currentVersion = new AtomicLong(-1L);

    @Override
    public Map<String, String> getConfig() {
        long version = instanceClient.getVersion(); // cheap probe
        return getConfigCached(version); // cache aware
    }

    @Override
    public String get(String key) {
        return getConfig().getOrDefault(key, "0");
    }

    @Cacheable(value = "instanceConfig", keyGenerator = "versionKeyGen", unless = "#result == null",
            sync = true)
    protected Map<String, String> getConfigCached(long version) {
        return instanceClient.getConfig().getValues();
    }


}
