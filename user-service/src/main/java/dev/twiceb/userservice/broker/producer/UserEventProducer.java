package dev.twiceb.userservice.broker.producer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import dev.twiceb.common.constants.KafkaTopicConstants;
import dev.twiceb.common.event.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class UserEventProducer implements KafkaMessageProducer {

    private final KafkaTemplate<String, UserEvent> kafka;

    @Override
    public void publish(UserEvent event) {
        String key = event.getId().toString();
        kafka.send(KafkaTopicConstants.USER_EVENTS_V1, key, event).whenComplete((res, ex) -> {
            if (ex != null) {
                log.error("Kafka publish failed for user {}: {}", key, ex.getMessage(), ex);
            } else if (res != null) {
                RecordMetadata md = res.getRecordMetadata();
                log.info("Published {} key={} to {}-{}@{}", event.getType(), key, md.topic(),
                        md.partition(), md.offset());
            }
        });
    }

}
