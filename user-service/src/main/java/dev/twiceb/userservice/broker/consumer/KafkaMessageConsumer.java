package dev.twiceb.userservice.broker.consumer;

import dev.twiceb.common.event.PasswordChangeEvent;

public interface KafkaMessageConsumer {
    void passwordChangeEventListener(PasswordChangeEvent passwordChangeEvent, String authId);
}
