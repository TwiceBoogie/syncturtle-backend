package dev.twiceb.taskservice.amqp;

import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.taskservice.model.Accounts;
import dev.twiceb.taskservice.repository.AccountsRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmqpConsumer {

    private final AccountsRepository accountsRepository;

    @SneakyThrows
    @RabbitListener(queues = "q.tasksvc")
    public void userCreatedListener(UserPrincipleResponse res) {
        if (accountsRepository.isAccountExist(res.getId())) {
            throw new RuntimeException("Account already exist");
        }

        Accounts account = new Accounts(res.getId(), res.getUserStatus(), res.getRole());

        accountsRepository.save(account);
    }
}
