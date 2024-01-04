package dev.twiceb.taskservice.repository.projection;

import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.taskservice.enums.TaskStatus;

public interface SubtaskProjection {
    Long getId();
    String getSubtaskTitle();
    String getSubtaskDescription();
    TaskStatus getStatus();
    PriorityStatus getPriority();
}
