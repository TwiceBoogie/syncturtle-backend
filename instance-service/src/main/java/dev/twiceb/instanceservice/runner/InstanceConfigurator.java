package dev.twiceb.instanceservice.runner;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import dev.twiceb.common.event.PlanEvent;
import dev.twiceb.common.event.PlanEvent.Type;
import dev.twiceb.instanceservice.broker.producer.PlanEventPublisher;
import dev.twiceb.instanceservice.domain.model.InstanceConfiguration;
import dev.twiceb.instanceservice.domain.model.Plan;
import dev.twiceb.instanceservice.domain.repository.InstanceConfigurationRepository;
import dev.twiceb.instanceservice.domain.repository.PlanRepository;
import dev.twiceb.instanceservice.service.util.ConfigurationHelper;
import dev.twiceb.instanceservice.shared.ConfigKeyRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstanceConfigurator {

    @Qualifier("encryptorBean")
    private final StringEncryptor encryptor;
    private final InstanceConfigurationRepository iConfigurationRepository;
    private final PlanRepository planRepository;
    private final PlanEventPublisher publisher;
    private final ConfigurationHelper cHelper;

    @Transactional
    public void run() {
        // Load full list of config keys
        List<ConfigKeyRecord> configKeys = cHelper.loadEnvConfigKeys();

        List<InstanceConfiguration> existingConfigs = iConfigurationRepository.findAll();
        Set<InstanceConfigurationKey> existingKeys = existingConfigs.stream()
                .map(InstanceConfiguration::getKey).collect(Collectors.toSet());

        List<InstanceConfiguration> toInsert =
                cHelper.loadMissingConfigKeys(configKeys, existingKeys);

        if (!toInsert.isEmpty()) {
            iConfigurationRepository.saveAll(toInsert);
        }

        if (!existingKeys.containsAll(ConfigurationHelper.INTEGRATION_FLAGS)) {
            // reuse existing configs to avoid refetching
            Map<InstanceConfigurationKey, InstanceConfiguration> configMap =
                    existingConfigs.stream().collect(
                            Collectors.toMap(InstanceConfiguration::getKey, Function.identity()));

            // save to db
            iConfigurationRepository.saveAll(cHelper.loadIntegrationFlags(configMap));
            Plan plan = planRepository.findByKey("free").orElseThrow();
            PlanEvent planEvent = PlanEvent.builder().type(Type.PLAN_CREATED).id(plan.getId())
                    .version(plan.getVersion()).updatedAt(null).schemaVersion(1).build();

            publisher.publish(planEvent);
            log.info("Integration flags loaded and saved");
        } else {
            ConfigurationHelper.INTEGRATION_FLAGS
                    .forEach(flag -> log.warn("{} configuration already exists", flag));
        }
    }
}
