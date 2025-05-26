package dev.twiceb.taskservice.dto.request;

import dev.twiceb.common.enums.EventStatus;
import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.common.validators.ValidPriorityStatus;
import dev.twiceb.common.validators.ValidStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Data
public class NewTaskRequest {
    @NotNull
    @NotBlank(message = EMPTY_TASK_TITLE)
    @Size(max = 100, message = EXCEED_TASK_TITLE_SIZE)
    private String name;

    @NotNull
    @NotBlank(message = EMPTY_TASK_DESC)
    @Size(max = 255, message = EXCEED_TASK_DESC_SIZE)
    private String description;

    @NotNull
    @ValidPriorityStatus
    private PriorityStatus priority;

    private LocalDateTime startDate;

    private LocalDateTime targetDate;

    private UUID parentId;

    private UUID projectId;

    private UUID moduleId;

    @NotNull
    @ValidStatus
    private EventStatus status;

    private TagsRequest tags;
}
