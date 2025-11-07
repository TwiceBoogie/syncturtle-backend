package dev.twiceb.passwordservice.amqp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.twiceb.common.dto.response.UserPrincipalResponse;

@Configuration
public class AmqpConsumerFallback {

    @Bean
    @ConditionalOnMissingBean
    MessagePublisher noopMessagePublisher() {
        return new MessagePublisher() {

            @Override
            public void userCreatedListener(UserPrincipalResponse res) {

            }

        };
    }
}
