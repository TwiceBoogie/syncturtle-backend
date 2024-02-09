package dev.twiceb.notificationservice.service;

import dev.twiceb.common.dto.request.NotificationRequest;
import dev.twiceb.notificationservice.repository.projection.UserNotificationProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Page<UserNotificationProjection> getUserNotifications(Long userId, Pageable pageable);
    void updateNotificationReadState(Long userId, Long notificationId);
    void updateAllNotificationReadState(Long userId);
    void sendNotification(NotificationRequest request);
}
