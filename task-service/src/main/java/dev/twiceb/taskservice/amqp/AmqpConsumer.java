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
public class AmqpConsumber {

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
