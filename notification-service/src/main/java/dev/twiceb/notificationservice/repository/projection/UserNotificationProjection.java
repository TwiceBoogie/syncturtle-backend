package dev.twiceb.notificationservice.repository.projection;

public interface UserNotificationProjection {
    Long getId();
    String getNotificationType();
    String getMessage();
}
