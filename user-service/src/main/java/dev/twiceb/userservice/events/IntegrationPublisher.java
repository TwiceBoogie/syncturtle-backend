package dev.twiceb.userservice.events;

import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import dev.twiceb.common.event.UserEvent;
import dev.twiceb.userservice.broker.producer.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class IntegrationPublisher {

    private final KafkaMessageProducer producer;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserChanged(UserChangedEvent event) {
        UserEvent payload = UserEvent.builder().eventId(UUID.randomUUID().toString())
                .occurredAt(event.getOccurredAt()).type(event.getType()).id(event.getUserId())
                .email(event.getEmail()).firstName(event.getFirstName())
                .lastName(event.getLastName()).displayName(event.getDisplayName())
                .dateJoined(event.getDateJoined()).version(event.getVersion()).build();

        producer.publish(payload);
    }
}
