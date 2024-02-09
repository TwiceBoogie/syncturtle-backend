package dev.twiceb.notificationservice.service.impl;

import dev.twiceb.common.dto.request.NotificationRequest;
import dev.twiceb.notificationservice.feign.UserClient;
import dev.twiceb.notificationservice.repository.NotificationRepository;
import dev.twiceb.notificationservice.repository.projection.UserNotificationProjection;
import dev.twiceb.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserClient userClient;

    @Override
    @Transactional
    public Page<UserNotificationProjection> getUserNotifications(Long userId, Pageable pageable) {
        return notificationRepository.getNotificationsByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public void updateNotificationReadState(Long userId, Long notificationId) {
        userClient.decreaseNotificationCount(userId);
        notificationRepository.updateNotificationReadState(notificationId, userId);
    }

    @Override
    public void updateAllNotificationReadState(Long userId) {
        userClient.resetNotificationCount(userId);
        notificationRepository.updateAllNotificationReadState(userId);
    }

    @Override
    public void sendNotification(NotificationRequest request) {

    }
}
