package dev.twiceb.taskservice.dto.response;

import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.taskservice.enums.TaskStatus;
import lombok.Data;

import java.util.Date;

@Data
public class TasksResponse {
    private Long id;
    private String taskTitle;
    private String taskDescription;
    private Date dueDate;
    private TaskStatus taskStatus;
    private PriorityStatus priority;
}
