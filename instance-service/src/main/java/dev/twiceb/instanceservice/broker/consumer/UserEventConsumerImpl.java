package dev.twiceb.instanceservice.broker.consumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import dev.twiceb.common.constants.KafkaTopicConstants;
import dev.twiceb.common.event.UserEvent;
import dev.twiceb.instanceservice.domain.model.UserLite;
import dev.twiceb.instanceservice.domain.repository.UserLiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true")
public class UserEventConsumerImpl {

    private final UserLiteRepository userLiteRepository;

    @KafkaListener(topics = KafkaTopicConstants.USER_EVENTS_V1, groupId = "user-svc-lite-v1", containerFactory = "userKafkaFactory")
    public void onUser(UserEvent event) {
        switch (event.getType()) {
            case USER_CREATED, USER_UPDATED -> upsert(event);
            case USER_SOFT_DELETED -> log.warn("user lite deletion not yet implemented");
        }
    }

    @Transactional
    private void upsert(UserEvent event) {
        UserLite existing = userLiteRepository.findById(event.getId()).orElse(null);
        if (existing != null && existing.getVersion() != null && event.getVersion() != null
                && event.getVersion() <= existing.getVersion()) {
            // older or same version so we ignore
            return;
        }

        UserLite user = new UserLite();
        user.setId(event.getId());
        user.setEmail(event.getEmail());
        user.setFirstName(event.getFirstName());
        user.setLastName(event.getLastName());
        user.setDisplayName(event.getDisplayName());
        user.setDateJoined(event.getDateJoined());
        user.setVersion(event.getVersion());

        userLiteRepository.save(user);
    }

}
