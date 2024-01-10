package dev.twiceb.taskservice.dto.response;

import dev.twiceb.common.enums.EventStatus;
import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.taskservice.enums.TaskStatus;
import lombok.Data;

@Data
public class SubtasksResponse {
    private Long id;
    private String subtaskTitle;
    private String subtaskDescription;
    private EventStatus status;
    private PriorityStatus priority;
}
