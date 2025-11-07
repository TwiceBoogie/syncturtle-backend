package dev.twiceb.passwordservice.broker.producer;

import java.time.Instant;
import java.util.UUID;

public interface KafkaMessageProducer {
    void sendPasswordChangeEvent(UUID authUserId, Instant expirationTime, UUID deviceKeyId);
}
