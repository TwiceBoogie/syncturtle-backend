package dev.twiceb.instanceservice.broker.producer;

import dev.twiceb.common.event.PlanEvent;

public interface PlanEventPublisher {
    void publish(PlanEvent event);
}
