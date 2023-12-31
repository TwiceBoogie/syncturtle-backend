package dev.twiceb.taskservice.dto.request;

import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.common.validators.ValidPriorityStatus;
import dev.twiceb.common.validators.ValidStatus;
import dev.twiceb.taskservice.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Data
public class NewTaskRequest {
    @NotBlank(message = EMPTY_TASK_TITLE)
    @Size(max = 100, message = EXCEED_TASK_TITLE_SIZE)
    private String taskTitle;

    @Size(max = 255, message = EXCEED_TASK_DESC_SIZE)
    private String taskDescriptions;

    @NotBlank(message = EMPTY_DUE_DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;

    @ValidStatus
    private TaskStatus status;

    @ValidPriorityStatus
    private PriorityStatus priority;

    private TagsRequest tags;

    private List<NewSubTaskRequest> subtasks;
}
