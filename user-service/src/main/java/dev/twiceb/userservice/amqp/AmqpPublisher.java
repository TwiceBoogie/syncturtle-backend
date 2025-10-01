package dev.twiceb.userservice.amqp;

import dev.twiceb.common.dto.request.EmailRequest;
import dev.twiceb.common.dto.response.UserPrincipalResponse;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.userservice.domain.projection.UserPrincipalProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmqpPublisher {

    private final AmqpTemplate amqpTemplate;
    private final BasicMapper basicMapper;
    private final Environment environment;
    // exchange is like a post office, queue is like the physical location
    // the routing key is the address of that location.
    @Value("${rabbitmq.exchanges.internal-fanout}")
    private String fanoutExchange;

    @Value("${rabbitmq.exchanges.internal-direct}")
    private String directExchange;

    @Value("${rabbitmq.routing-keys.internal-mail}")
    private String routingKey;

    public void userCreated(UserPrincipalProjection user) {
        if (isTestEnvironment()) {
            log.info("==> Skipping AMQP message send in test environment.");
            return;
        }

        log.info("==> converting and sending to amqp exchange");
        UserPrincipalResponse userData =
                basicMapper.convertToResponse(user, UserPrincipalResponse.class);
        amqpTemplate.convertAndSend(this.fanoutExchange, "", userData);
    }

    public void sendEmail(EmailRequest emailRequest) {
        if (isTestEnvironment()) {
            log.info("==> Skipping email send in test environment.");
            return;
        }
        try {
            amqpTemplate.convertAndSend(this.directExchange, this.routingKey, emailRequest);
        } catch (AmqpException ex) {
            log.error("==> Error sending email: {}", ex.getMessage());
            // TODO: perhaps create a custom exception for this
            throw new ApiRequestException("Failed to send email. Please try again later.",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private boolean isTestEnvironment() {
        return Arrays.asList(environment.getActiveProfiles()).contains("test");
    }
}
