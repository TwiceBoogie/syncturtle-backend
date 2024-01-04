package dev.twiceb.taskservice.repository.projection;

import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.common.model.Tags;
import dev.twiceb.taskservice.enums.TaskStatus;

import java.util.Date;
import java.util.List;

public interface TaskProjection {
    Long getId();
    String getTaskTitle();
    String getTaskDescription();
    Date getDueDate();
    PriorityStatus getPriority();
    TaskStatus getTaskStatus();
    List<Tags> getTags();
}
