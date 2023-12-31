package dev.twiceb.taskservice.model;

import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.taskservice.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SubTasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Tasks task;

    @Column(name = "subtask_title")
    private String subtaskTitle;

    @Column(name = "subtask_description")
    private String subtaskDescription;

    @Column(name = "status", nullable = false)
    private TaskStatus status;

    @Column(name = "priority", nullable = false)
    private PriorityStatus priority;

    public SubTasks() {}

    public SubTasks(Tasks task, String subtaskTitle, String subtaskDescription) {
        this.task = task;
        this.subtaskTitle = subtaskTitle;
        this.subtaskDescription = subtaskDescription;
    }

    @PrePersist
    private void prePersist() {
        this.status = TaskStatus.TODO;
        this.priority = PriorityStatus.NONE;
    }
}
