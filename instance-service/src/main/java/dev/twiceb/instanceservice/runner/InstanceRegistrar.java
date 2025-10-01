package dev.twiceb.instanceservice.runner;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.common.event.InstanceEvent;
import dev.twiceb.common.event.InstanceEvent.Type;
import dev.twiceb.instanceservice.broker.producer.InstanceEventPublisher;
import dev.twiceb.instanceservice.domain.model.Instance;
import dev.twiceb.instanceservice.domain.repository.InstanceRepository;
import dev.twiceb.instanceservice.service.util.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstanceRegistrar {

    private final AppProperties appProperties;
    private final BuildProperties buildProperties;
    private final InstanceRepository instanceRepository;
    private final InstanceEventPublisher publisher;

    @Transactional
    public void run(String machineSignature) {
        // check if instance is registerd
        Instance instance =
                instanceRepository.findFirstByOrderByCreatedAtAsc(Instance.class).orElse(null);

        // if instance is null then register this instance
        if (instance == null) {
            instance = Instance.register(buildProperties.getVersion(), buildProperties.getVersion(),
                    machineSignature, appProperties.isTest());

            instance = instanceRepository.save(instance);

            InstanceEvent event = InstanceEvent.builder().type(Type.INSTANCE_CREATED)
                    .id(instance.getId()).slug(instance.getSlug()).edition(instance.getEdition())
                    .version(instance.getVersion()).updatedAt(instance.getUpdatedAt())
                    .schemaVersion(1).build();

            publisher.publish(event);
            log.info("New instance registered with signature: " + machineSignature);
        } else {
            // update instance details
            instance.updateInstanceDetails(buildProperties.getVersion(),
                    buildProperties.getVersion(), appProperties.isTest());
            instance = instanceRepository.save(instance);

            InstanceEvent event = InstanceEvent.builder().type(Type.INSTANCE_UPDATED)
                    .id(instance.getId()).slug(instance.getSlug()).edition(instance.getEdition())
                    .version(instance.getVersion()).updatedAt(instance.getUpdatedAt())
                    .schemaVersion(1).build();
            publisher.publish(event);
            log.info("Instance already registered - updating");
        }

    }
}
