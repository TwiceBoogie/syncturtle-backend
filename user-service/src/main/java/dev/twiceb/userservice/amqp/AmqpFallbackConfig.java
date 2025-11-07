package dev.twiceb.userservice.amqp;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.userservice.domain.projection.UserPrincipalProjection;

@Configuration
public class AmqpFallbackConfig {

    @Bean
    @ConditionalOnMissingBean(MessagePublisher.class)
    MessagePublisher noopAmqpPublisher() {
        return new MessagePublisher() {

            @Override
            public void userCreated(UserPrincipalProjection user) {

            }

            @Override
            public void sendEmail(EmailRequest emailRequest) {

            }

        };
    }
}
