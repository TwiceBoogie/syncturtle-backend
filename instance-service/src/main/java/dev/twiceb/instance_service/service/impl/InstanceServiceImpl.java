package dev.twiceb.instance_service.service.impl;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.instance_service.ConfigKeyLookupRecord;
import dev.twiceb.instance_service.repository.InstanceConfigurationRepository;
import dev.twiceb.instance_service.repository.InstanceRepository;
import dev.twiceb.instance_service.repository.projection.InstanceProjection;
import dev.twiceb.instance_service.service.InstanceService;
import dev.twiceb.instance_service.util.ConfigurationHelper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InstanceServiceImpl implements InstanceService {

    private final InstanceRepository instanceRepository;
    private final InstanceConfigurationRepository iConfigurationRepository;
    private final ConfigurationHelper cHelper;

    @Override
    @Transactional
    public InstanceProjection getInstancePrinciple() {
        return instanceRepository.findProjectedFirstByOrderByCreatedAtAsc().orElse(null);
    }

    @Override
    public Map<String, String> getConfigurationValues() {
        Map<String, String> config = cHelper
                .getConfigurationValues(List.of(new ConfigKeyLookupRecord("ENABLE_SIGNUP", "0"),
                        new ConfigKeyLookupRecord("IS_GOOGLE_ENABLED", "0"),
                        new ConfigKeyLookupRecord("IS_GITHUB_ENABLED", "0"),
                        new ConfigKeyLookupRecord("GITHUB_APP_NAME", ""),
                        new ConfigKeyLookupRecord("IS_GITLAB_ENABLED", "0"),
                        new ConfigKeyLookupRecord("EMAIL_HOST", ""),
                        new ConfigKeyLookupRecord("ENABLE_MAGIC_LINK_LOGIN", "1"),
                        new ConfigKeyLookupRecord("ENABLE_EMAIL_PASSWORD", "1")));
        return config;
    }

}
