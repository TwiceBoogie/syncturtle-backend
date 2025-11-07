package dev.twiceb.userservice.broker.consumer;

import dev.twiceb.common.event.PasswordChangeEvent;
import dev.twiceb.userservice.service.UserActionHandlerService;
import lombok.RequiredArgsConstructor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static dev.twiceb.common.constants.KafkaTopicConstants.PASSWORD_CHANGE_TOPIC;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_ID_HEADER;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.kafka.enabled", havingValue = "true", matchIfMissing = true)
public class PasswordChangeConsumer implements KafkaMessageConsumer {

    private final UserActionHandlerService service;

    @Override
    @KafkaListener(topics = PASSWORD_CHANGE_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void passwordChangeEventListener(PasswordChangeEvent passwordChangeEvent,
            @Header(AUTH_USER_ID_HEADER) String authId) {
        service.handlePasswordChangeEvent(passwordChangeEvent, UUID.fromString(authId));
    }
}
