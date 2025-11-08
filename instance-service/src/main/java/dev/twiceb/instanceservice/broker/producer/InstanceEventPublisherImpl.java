package dev.twiceb.instanceservice.broker.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import dev.twiceb.common.constants.KafkaTopicConstants;
import dev.twiceb.common.event.InstanceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true")
public class InstanceEventPublisherImpl implements InstanceEventPublisher {

    private final KafkaTemplate<String, InstanceEvent> kafka;

    @Override
    public void publish(InstanceEvent event) {
        String key = event.getId().toString(); // partitioning key
        kafka.send(KafkaTopicConstants.INSTANCE_EVENTS_V1, key, event).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Kafka publish failed for instance {}: {}", key, ex.getMessage(), ex);
            } else if (res != null) {
                RecordMetadata md = res.getRecordMetadata();
                log.info("Published {} key={} to {}-{}@{}", event.getType(), key, md.topic(),
                        md.partition(), md.offset());
            }
        });
    }
}
