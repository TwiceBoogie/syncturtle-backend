package dev.twiceb.common.dto.request;

import dev.twiceb.common.validators.ValidRecurrence;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Data
@ValidRecurrence
public class NewRecurringEventRequest {
    @NotBlank(message = EMPTY_TASK_TITLE)
    @Size(max = 100, message = EXCEED_TASK_TITLE_SIZE)
    private String title;

    @NotBlank(message = EMPTY_TASK_DESC)
    @Size(max = 255, message = EXCEED_TASK_DESC_SIZE)
    private String Description;

    @Future(message = "Recurrence end date must be in the future or none.")
    private Date recurrenceEndDate;

    @NotBlank(message = EMPTY_RECURRENCE_PATTERN)
    @Pattern(regexp = "(every day|every week|every month|custom)", message = "Invalid recurrence pattern")
    private String recurrencePattern;

    private Integer recurrenceFreq;
}
