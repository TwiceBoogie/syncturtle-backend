package dev.twiceb.taskservice.model;

import dev.twiceb.common.enums.EventStatus;
import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.common.model.Tags;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Accounts account;

    @Column(name = "task_title", nullable = false)
    private String taskTitle;

    @Column(name = "task_description", nullable = false)
    private String taskDescription;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "priority", nullable = false)
    @ColumnTransformer(
            read = "priority",
            write = "?::priority_status"
    )
    private PriorityStatus priority = PriorityStatus.NONE;

    @Column(name = "status", nullable = false)
    @ColumnTransformer(
            read = "status",
            write = "?::task_status"
    )
    private EventStatus taskStatus = EventStatus.IN_PROGRESS;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaskAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTasks> subtasks = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "task_tags",
            joinColumns = {@JoinColumn(name = "task_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private List<Tags> tags = new ArrayList<>();

    public Task() {}

    public Task(Accounts account, String taskTitle, String taskDescription, LocalDate dueDate) {
        this.account = account;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.dueDate = dueDate;
    }
}
