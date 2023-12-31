package dev.twiceb.taskservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class RecurringTasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Accounts account;

    @Column(name = "task_title")
    private String taskTitle;

    @Column(name = "task_description")
    private String taskDescription;

    @Column(name = "recurrence_pattern", nullable = false)
    private String recurrencePattern;

    @Column(name = "recurrence_end_date")
    private Date recurrenceEndDate;

    public RecurringTasks() {}

    public RecurringTasks(Accounts account, String taskTitle, String taskDescription, String recurrencePattern) {
        this.account = account;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.recurrencePattern = recurrencePattern;
    }
}
