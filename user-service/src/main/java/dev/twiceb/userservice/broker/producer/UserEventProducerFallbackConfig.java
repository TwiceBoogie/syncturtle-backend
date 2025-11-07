package dev.twiceb.userservice.broker.producer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.twiceb.common.event.PasswordChangeEvent;
import dev.twiceb.common.event.UserEvent;
import dev.twiceb.userservice.broker.consumer.KafkaMessageConsumer;

@Configuration
public class UserEventProducerFallbackConfig {

    @Bean
    @ConditionalOnMissingBean
    KafkaMessageProducer noopKafkaMessageProducer() {
        return new KafkaMessageProducer() {

            @Override
            public void publish(UserEvent event) {

            }

        };
    }

    @Bean
    @ConditionalOnMissingBean
    KafkaMessageConsumer noopKafkaMessageConsumer() {
        return new KafkaMessageConsumer() {

            @Override
            public void passwordChangeEventListener(PasswordChangeEvent passwordChangeEvent, String authId) {

            }

        };
    }
}
