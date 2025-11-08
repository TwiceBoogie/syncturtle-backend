package dev.twiceb.instanceservice.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.twiceb.common.event.InstanceEvent;
import dev.twiceb.instanceservice.broker.producer.InstanceEventPublisher;
import dev.twiceb.instanceservice.broker.producer.InstanceEventPublisherImpl;

@Configuration
@ConditionalOnMissingBean(InstanceEventPublisherImpl.class)
public class InstanceEventPublisherFallback {

    @Bean
    InstanceEventPublisher noopInstanceEventPublisher() {
        return new InstanceEventPublisher() {

            @Override
            public void publish(InstanceEvent event) {

            }

        };
    }
}
