package dev.twiceb.instanceservice.service;

import java.util.Map;
import dev.twiceb.instanceservice.repository.projection.InstanceProjection;

public interface InstanceService {
    InstanceProjection getInstancePrinciple();

    Map<String, String> getConfigurationValues();

    long getInstanceVersion();
}
