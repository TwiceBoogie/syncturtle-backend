package dev.twiceb.taskservice.model;

import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.common.model.Tags;
import dev.twiceb.taskservice.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Tasks {

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
    private Date dueDate;

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
    private TaskStatus taskStatus = TaskStatus.IN_PROGRESS;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTasks> subtasks;

    @ManyToMany
    @JoinTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tags> tags;

    public Tasks() {}

    public Tasks(Accounts account, String taskTitle, String taskDescription, Date dueDate) {
        this.account = account;
        this.taskTitle = taskTitle;
        this.taskDescription = taskDescription;
        this.dueDate = dueDate;
    }
}
