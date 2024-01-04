package dev.twiceb.taskservice.dto.response;

import lombok.Data;

import java.util.Date;

@Data
public class RecurringTasksResponse {
    private Long id;
    private String taskTitle;
    private String taskDescription;
    private String recurrencePattern;
    private Integer recurrenceFreq;
    private Date recurrenceEndDate;
}
