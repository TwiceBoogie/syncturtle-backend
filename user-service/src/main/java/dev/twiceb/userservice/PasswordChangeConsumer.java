package dev.twiceb.userservice;

import dev.twiceb.common.event.PasswordChangeEvent;
import dev.twiceb.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static dev.twiceb.common.constants.KafkaTopicConstants.PASSWORD_CHANGE_TOPIC;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_ID_HEADER;

//@Component
//@RequiredArgsConstructor
//public class PasswordChangeConsumer {
//
//    private final UserService userService;
//
//    @KafkaListener(topics = PASSWORD_CHANGE_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
//    public void passwordChangeEventListener(PasswordChangeEvent passwordChangeEvent,
//                                            @Header(AUTH_USER_ID_HEADER) String authId) {
//        userService.handlePasswordChangeEvent(passwordChangeEvent, authId);
//    }
//}
