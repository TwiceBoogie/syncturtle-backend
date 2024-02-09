package dev.twiceb.notificationservice.dto.response;

import lombok.Data;

@Data
public class NotificationResponse {
    private Long id;
    private String notificationType;
    private String message;
}
