package dev.twiceb.userservice.amqp;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.repository.projection.UserPrincipalProjection;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmqpPublisher {

    private static final Logger logger = LoggerFactory.getLogger(AmqpPublisher.class);

    private final AmqpTemplate amqpTemplate;
    private final BasicMapper basicMapper;
    // exchange is like a post office, queue is like the physical location
    // the routing key is the address of that location.
    @Value("${rabbitmq.exchanges.internal-fanout}")
    private String fanoutExchange;

    @Value("${rabbitmq.exchanges.internal-direct}")
    private String directExchange;

    @Value("${rabbitmq.routing-keys.internal-mail}")
    private String routingKey;

    public void userCreated(UserPrincipalProjection user) {
        logger.info("converting and sending to amqp exchange");
        UserPrincipleResponse userData = basicMapper.convertToResponse(user,
                UserPrincipleResponse.class);
        amqpTemplate.convertAndSend(this.fanoutExchange, "", userData);
    }

    public void sendEmail(EmailRequest emailRequest) {
        amqpTemplate.convertAndSend(this.directExchange, this.routingKey, emailRequest);
    }
}
