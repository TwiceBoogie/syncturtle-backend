package dev.twiceb.instanceservice.broker.producer;

import dev.twiceb.common.event.InstanceEvent;

public interface InstanceEventPublisher {
    void publish(InstanceEvent event);
}
