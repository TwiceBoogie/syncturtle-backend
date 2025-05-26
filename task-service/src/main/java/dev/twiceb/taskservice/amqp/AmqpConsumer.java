package dev.twiceb.taskservice.amqp;

import dev.twiceb.common.dto.response.UserPrincipleResponse;
import dev.twiceb.taskservice.model.User;
import dev.twiceb.taskservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmqpConsumer {

    private final UserRepository userRepository;

    @SneakyThrows
    @RabbitListener(queues = "q.tasksvc")
    public void userCreatedListener(UserPrincipleResponse res) {
        if (userRepository.isAccountExist(res.getId())) {
            throw new RuntimeException("Account already exist");
        }

        User account = new User(res.getId(), res.getUserStatus(), res.getRole());

        userRepository.save(account);
    }
}
