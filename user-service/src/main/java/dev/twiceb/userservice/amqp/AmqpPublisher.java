package dev.twiceb.userservice.amqp;

import dev.twiceb.common.dto.request.EmailRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AmqpPublisher {

    private static final Logger logger = LoggerFactory.getLogger(AmqpPublisher.class);

    private final AmqpTemplate amqpTemplate;

    @Value("${rabbitmq.exchanges.internal}")
    private String exchange;

    @Value("${rabbitmq.routing-keys.internal-mail}")
    private String routingKey;

    public AmqpPublisher(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public void sendEmail(EmailRequest emailRequest) {
        logger.info("converting and sending to amqp exchange");
        amqpTemplate.convertAndSend(exchange, routingKey, emailRequest);
    }
}
