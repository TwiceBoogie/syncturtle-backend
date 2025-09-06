package dev.twiceb.userservice.service;

import java.util.Map;
import dev.twiceb.common.enums.InstanceConfigurationKey;

public interface FeatureFlagService {

    Map<InstanceConfigurationKey, String> getConfig();

    public String get(InstanceConfigurationKey key);
}
