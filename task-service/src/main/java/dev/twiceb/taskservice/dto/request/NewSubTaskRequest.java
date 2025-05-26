package dev.twiceb.taskservice.dto.request;

import dev.twiceb.common.enums.EventStatus;
import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.common.validators.ValidPriorityStatus;
import dev.twiceb.common.validators.ValidStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Data
public class NewSubTaskRequest {
    @NotBlank(message = EMPTY_SUBTASK_TITLE)
    @Size(max = 100, message = EXCEED_SUBTASK_TITLE_SIZE)
    private String name;

    @NotBlank(message = EMPTY_SUBTASK_TITLE)
    @Size(max = 100, message = EXCEED_SUBTASK_DESC_SIZE)
    private String description;

    private LocalDateTime targetDate;

    @ValidStatus
    private EventStatus status;

    @ValidPriorityStatus
    private PriorityStatus priority;
}
