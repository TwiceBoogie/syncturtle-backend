package dev.twiceb.userservice.broker.producer;

import dev.twiceb.common.event.UserEvent;

public interface KafkaMessageProducer {
    void publish(UserEvent event);
}
