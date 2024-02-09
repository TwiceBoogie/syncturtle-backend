package dev.twiceb.common.dto.request;

import dev.twiceb.common.enums.PriorityStatus;
import lombok.Data;

@Data
public class NotificationRequest {
    private Long userId;
    private String notificationType;
    private String message;
}
