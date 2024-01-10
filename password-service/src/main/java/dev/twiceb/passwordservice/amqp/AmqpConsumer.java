package dev.twiceb.passwordservice.amqp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
    private final AccountsRepository accountsRepository;

    @SneakyThrows
    @RabbitListener(queues = "q.passwordsvc")
    public void userCreatedListener(UserPrincipleResponse res) {
        if (accountsRepository.isAccountExist(res.getId())) {
            logger.error("Error from user-svc to password-svc", new RuntimeException("User already exists"));
        }

        Accounts account = new Accounts(res.getId(), res.getUserStatus(), res.getRole());

        accountsRepository.save(account);
    }
}
