package dev.twiceb.taskservice.dto.request;

import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.common.validators.ValidPriorityStatus;
import dev.twiceb.common.validators.ValidStatus;
import dev.twiceb.taskservice.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

import static dev.twiceb.common.constants.ErrorMessage.*;
import static dev.twiceb.common.constants.ErrorMessage.EMPTY_DUE_DATE;

@Data
public class UpdateSubtaskRequest {
    @NotNull
    @Pattern(regexp = "^\\d+$", message = INVALID_ID_PROVIDED)
    private Long id;

    @NotBlank(message = EMPTY_SUBTASK_TITLE)
    @Size(max = 100, message = EXCEED_SUBTASK_TITLE_SIZE)
    private String subtaskTitle;

    @NotBlank(message = EMPTY_SUBTASK_TITLE)
    @Size(max = 100, message = EXCEED_SUBTASK_DESC_SIZE)
    private String subtaskDescription;

    @NotBlank(message = EMPTY_DUE_DATE)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dueDate;

    @ValidStatus
    private TaskStatus status;

    @ValidPriorityStatus
    private PriorityStatus priority;
}
