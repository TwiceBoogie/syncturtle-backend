package dev.twiceb.passwordservice.producer;

import dev.twiceb.common.event.PasswordChangeEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static dev.twiceb.common.constants.KafkaTopicConstants.PASSWORD_CHANGE_TOPIC;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_ID_HEADER;

//@Component
//@RequiredArgsConstructor
//public class PasswordChangeProducer {
//
//    private final KafkaTemplate<String, PasswordChangeEvent> kafkaTemplate;
//
//    public void sendPasswordChangeEvent(Long authUserId, LocalDateTime expirationTime, String deviceKey) {
//        kafkaTemplate.send(getPasswordChangeEvent(PASSWORD_CHANGE_TOPIC, authUserId, expirationTime, deviceKey));
//    }
//
//    private static ProducerRecord<String, PasswordChangeEvent> getPasswordChangeEvent(
//            String topic,
//            Long authUserId,
//            LocalDateTime expirationTime,
//            String deviceKey) {
//        ProducerRecord<String, PasswordChangeEvent> producerRecord = new ProducerRecord<>(
//                topic, toPasswordChangeEvent(expirationTime, deviceKey)
//        );
//        producerRecord.headers().add(AUTH_USER_ID_HEADER, authUserId.toString().getBytes(StandardCharsets.UTF_8));
//        return producerRecord;
//    }
//
//    private static PasswordChangeEvent toPasswordChangeEvent(LocalDateTime expirationTime, String deviceKey) {
//        return PasswordChangeEvent.builder()
//                .expirationTime(expirationTime)
//                .deviceKeyId(1L)
//                .build();
//    }
//}
