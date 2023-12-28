package dev.twiceb.passwordservice.amqp;

import java.nio.file.attribute.UserPrincipal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.passwordservice.model.Accounts;
import dev.twiceb.passwordservice.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Component
@RequiredArgsConstructor
public class AmqpConsumer {

    private final Logger logger = LoggerFactory.getLogger(AmqpConsumer.class);
    private final AmqpTemplate amqpTemplate;
    private final AccountsRepository accountsRepository;

    @Value("${rabbitmq.exchanges.internal-fanout}")
    private String fanoutExchange;

    @Value("${rabbitmq.queues.internal-fanout-queue}")
    private String fanoutQueue;

    @SneakyThrows
    @RabbitListener(queues = "q.passwordsvc")
    public void userCreatedListener(UserPrincipleResponse res) {
        if (accountsRepository.isAccountExist(res.getId())) {
            logger.error("Error from user-svc to password-svc", new RuntimeException("User already exists"));
        }

        Accounts account = new Accounts();
        account.setUserId(res.getId());

        accountsRepository.save(account);
    }
}
