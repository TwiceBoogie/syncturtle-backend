package dev.twiceb.taskservice.repository.projection;

import java.util.Date;

public interface RecurringTaskProjection {
    Long getId();
    String getTaskTitle();
    String getTaskDescription();
    String getRecurrencePattern();
    Integer getRecurrenceFreq();
    Date getRecurrenceEndDate();
}
