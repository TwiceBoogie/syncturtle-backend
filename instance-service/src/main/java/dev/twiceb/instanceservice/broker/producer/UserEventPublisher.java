// package dev.twiceb.instanceservice.broker.producer;

// import org.springframework.context.annotation.Profile;
// import org.springframework.kafka.core.KafkaTemplate;
// import org.springframework.kafka.support.KafkaHeaders;
// import org.springframework.messaging.Message;
// import org.springframework.messaging.support.MessageBuilder;
// import org.springframework.stereotype.Service;
// import dev.twiceb.common.dto.context.TraceContext;
// import dev.twiceb.common.event.UserCreateEvent;
// import lombok.RequiredArgsConstructor;

// @Profile("!setup")
// @Service
// @RequiredArgsConstructor
// public class UserEventPublisher {

// private final KafkaTemplate<String, UserCreateEvent> userCreateTemplate;

// public void publishUserCreate(UserCreateEvent event, TraceContext trace) {
// Message<UserCreateEvent> msg = MessageBuilder.withPayload(event)
// .setHeader(KafkaHeaders.TOPIC, "user.create.request")
// .setHeader(KafkaHeaders.KEY, event.getEmail().toLowerCase())
// .setHeader("event-type", "user.create.request")
// .setHeader("schema-version", trace.getSchemaVersion())
// .setHeader("correlation-id", trace.getCorrelationId())
// .setHeader("indempotency-key", trace.getIdempotencyKey())
// .setHeader("occured-at", String.valueOf(System.currentTimeMillis()))
// .setHeader("producer", trace.getProducer()).build();

// userCreateTemplate.send(msg);
// }

// }
