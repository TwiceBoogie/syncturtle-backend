package dev.twiceb.passwordservice.broker.producer;

import java.time.Instant;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PasswordChangeProducerFallback {

    @Bean
    @ConditionalOnMissingBean
    KafkaMessageProducer noopKafkaMessageProducer() {
        return new KafkaMessageProducer() {

            @Override
            public void sendPasswordChangeEvent(UUID authUserId, Instant expirationTime, UUID deviceKeyId) {

            }

        };
    }
}
