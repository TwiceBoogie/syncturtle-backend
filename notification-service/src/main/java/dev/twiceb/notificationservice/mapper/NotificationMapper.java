package dev.twiceb.notificationservice.mapper;

import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.notificationservice.dto.response.NotificationResponse;
import dev.twiceb.notificationservice.repository.projection.UserNotificationProjection;
import dev.twiceb.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationMapper {

    private final BasicMapper mapper;
    private final NotificationService notificationService;

    public HeaderResponse<NotificationResponse> getUserNotifications(Long userId, Pageable pageable) {
        Page<UserNotificationProjection> notifications = notificationService.getUserNotifications(userId, pageable);
        return mapper.getHeaderResponse(notifications, NotificationResponse.class);
    }

    public void updateNotificationReadState(Long userId, Long notificationId) {
        notificationService.updateNotificationReadState(userId, notificationId);
    }

    public void updateAllNotificationReadState(Long userId) {
        notificationService.updateAllNotificationReadState(userId);
    }
}
