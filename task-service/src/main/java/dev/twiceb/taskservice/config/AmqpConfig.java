package dev.twiceb.taskservice.amqp;

import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.taskservice.model.Accounts;
import dev.twiceb.taskservice.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AmqpConfig {

    private final AmqpTemplate amqpTemplate;
    private final AccountsRepository accountsRepository;

    @Value("${rabbitmq.exchanges.internal-fanout}")
    private String fanoutExchange;

    @Value("${rabbitmq.queues.internal-fanout-queue}")
    private String fanoutQueue;

    @SneakyThrows
    @RabbitListener(queues = "q.tasksvc")
    public void userCreatedListener(UserPrincipleResponse res) {
        if (accountsRepository.isAccountExist(res.getId())) {
            throw new RuntimeException("User already exist.");
        }

        Accounts account = new Accounts();
        account.setUserId(res.getId());


        accountsRepository.save(account);
    }
}
