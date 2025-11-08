package dev.twiceb.instanceservice.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.twiceb.common.event.PlanEvent;
import dev.twiceb.instanceservice.broker.producer.PlanEventPublisher;
import dev.twiceb.instanceservice.broker.producer.PlanEventPublisherImpl;

@Configuration
@ConditionalOnMissingBean(PlanEventPublisherImpl.class)
public class PlanEventPublisherFallback {

    @Bean
    PlanEventPublisher noopPlanEventPublisher() {
        return new PlanEventPublisher() {

            @Override
            public void publish(PlanEvent event) {

            }

        };
    }
}
