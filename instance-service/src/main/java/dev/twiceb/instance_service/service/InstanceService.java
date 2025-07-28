package dev.twiceb.instance_service.service;

import java.util.Map;
import dev.twiceb.instance_service.repository.projection.InstanceProjection;

public interface InstanceService {
    InstanceProjection getInstancePrinciple();

    Map<String, String> getConfigurationValues();
}
