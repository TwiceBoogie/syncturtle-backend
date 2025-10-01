package dev.twiceb.workspace_service.broker;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import dev.twiceb.common.constants.KafkaTopicConstants;
import dev.twiceb.common.event.InstanceEvent;
import dev.twiceb.common.event.PlanEvent;
import dev.twiceb.workspace_service.service.CatalogProjector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatalogListener {

    private final CatalogProjector projector;

    @KafkaListener(topics = KafkaTopicConstants.INSTANCE_EVENTS_V1,
            groupId = "workspace-svc-catalog-v1", containerFactory = "instanceKafkaFactory")
    void onInstance(InstanceEvent event) {
        switch (event.getType()) {
            case INSTANCE_CREATED, INSTANCE_UPDATED -> projector.applyInstanceUpsert(event.getId(),
                    event.getSlug(), event.getEdition(), event.getVersion(), event.getUpdatedAt());
            case INSTANCE_SOFT_DELETED -> log.warn("Not yet implemented");
        }
    }

    @KafkaListener(topics = KafkaTopicConstants.PLAN_EVENTS_V1,
            groupId = "workspace-svc-catalog-v1", containerFactory = "planKafkaFactory")
    void onPlan(PlanEvent event) {
        switch (event.getType().toString()) {
            case "PLAN_CREATED", "PLAN_UPDATED":
                projector.applyPlanUpsert(event.getId(), event.getVersion(), event.getUpdatedAt());
                break;

            default:
                break;
        }
    }

}
