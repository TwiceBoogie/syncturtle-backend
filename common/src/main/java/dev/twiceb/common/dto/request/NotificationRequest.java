package dev.twiceb.common.dto.request;

// import dev.twiceb.common.enums.PriorityStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class NotificationRequest {
    private UUID userId;
    private String notificationType;
    private String message;
}
