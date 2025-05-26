package dev.twiceb.taskservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "recurring_tasks")
public class RecurringTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private User account;

    @Column(name = "task_title", nullable = false)
    private String taskTitle;

    @Column(name = "task_description", nullable = false)
    private String taskDescription;

    @Column(name = "recurrence_pattern", nullable = false)
    private String recurrencePattern;

    @Column(name = "recurrence_freq")
    private Integer recurrenceFreq;

    @Column(name = "recurrence_end_date")
    private Date recurrenceEndDate;

    public RecurringTask() {}

    public RecurringTask(User account, String taskTitle, String taskDescription, String recurrencePattern) {
        this.account = account;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.recurrencePattern = recurrencePattern;
    }
}
