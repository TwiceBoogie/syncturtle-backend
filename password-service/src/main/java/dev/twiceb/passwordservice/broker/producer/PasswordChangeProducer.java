package dev.twiceb.passwordservice.broker.producer;

import dev.twiceb.common.event.PasswordChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static dev.twiceb.common.constants.KafkaTopicConstants.PASSWORD_CHANGE_TOPIC;
import static dev.twiceb.common.constants.PathConstants.AUTH_USER_ID_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class PasswordChangeProducer {

    private final KafkaTemplate<String, PasswordChangeEvent> kafkaTemplate;
    private static final int MAX_RETRIES = 3;

    public void sendPasswordChangeEvent(UUID authUserId, Instant expirationTime, UUID deviceKeyId) {
        ProducerRecord<String, PasswordChangeEvent> record = getPasswordChangeEvent(
                PASSWORD_CHANGE_TOPIC, authUserId, expirationTime, deviceKeyId);
        sendWithRetry(record, 1);
    }

    private void sendWithRetry(ProducerRecord<String, PasswordChangeEvent> record, int attempt) {
        CompletableFuture<SendResult<String, PasswordChangeEvent>> future =
                kafkaTemplate.send(record);
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                handleSuccess(result.getProducerRecord(), result.getRecordMetadata());
            } else {
                if (attempt <= MAX_RETRIES) {
                    log.warn("Failed to send message for key {}, retrying (attempt {})",
                            record.key(), attempt);
                    sendWithRetry(record, attempt + 1);
                } else {
                    handleFailure(result.getProducerRecord(), ex);
                }
            }
        });
    }

    private static ProducerRecord<String, PasswordChangeEvent> getPasswordChangeEvent(String topic,
            UUID authUserId, Instant expirationTime, UUID deviceKeyId) {
        // Adding metadata
        PasswordChangeEvent event = toPasswordChangeEvent(expirationTime, deviceKeyId);
        ProducerRecord<String, PasswordChangeEvent> producerRecord =
                new ProducerRecord<>(topic, event);

        producerRecord.headers().add(AUTH_USER_ID_HEADER,
                authUserId.toString().getBytes(StandardCharsets.UTF_8));
        producerRecord.headers().add("timestamp",
                String.valueOf(Instant.now().toEpochMilli()).getBytes(StandardCharsets.UTF_8));
        // What's the reason for this?
        producerRecord.headers().add("correlationId",
                UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));

        return producerRecord;
    }

    private static PasswordChangeEvent toPasswordChangeEvent(Instant expirationTime,
            UUID deviceKeyId) {
        return PasswordChangeEvent.builder().expirationTime(expirationTime).deviceKeyId(deviceKeyId)
                .build();
    }

    private void handleSuccess(ProducerRecord<String, PasswordChangeEvent> record,
            RecordMetadata metadata) {
        log.info("Message sent successfully for key {} with partition {} and offset {}",
                record.key(), metadata.partition(), metadata.offset());
    }

    private void handleFailure(ProducerRecord<String, PasswordChangeEvent> record, Throwable ex) {
        log.error("Error sending message for key {}: {}", record.key(), ex.getMessage());
    }
}
