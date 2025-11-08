package dev.twiceb.instanceservice.broker.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import dev.twiceb.common.constants.KafkaTopicConstants;
import dev.twiceb.common.event.PlanEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlanEventPublisherImpl implements PlanEventPublisher {

    private final KafkaTemplate<String, PlanEvent> kafka;

    @Override
    public void publish(PlanEvent event) {
        String key = event.getId().toString();
        kafka.send(KafkaTopicConstants.PLAN_EVENTS_V1, key, event).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Kafka publish failed for plan {}: {}", key, ex.getMessage(), ex);
            } else if (res != null) {
                RecordMetadata md = res.getRecordMetadata();
                log.info("Published {} key={} to {}-{}@{}", event.getType(), key, md.topic(),
                        md.partition(), md.offset());
            }
        });
    }

}
