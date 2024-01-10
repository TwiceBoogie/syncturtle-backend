package dev.twiceb.taskservice.model;

import dev.twiceb.common.enums.EventStatus;
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
    @JoinColumn(name = "task_id", nullable = false)
    private Tasks task;

    @Column(name = "subtask_title", nullable = false)
    private String subtaskTitle;

    @Column(name = "subtask_description", nullable = false)
    private String subtaskDescription;

    @Column(name = "status", nullable = false)
    private EventStatus status;

    @Column(name = "priority", nullable = false)
    private PriorityStatus priority;

    public SubTasks(Tasks task, String title, String description) {
        this.task = task;
        this.subtaskTitle = title;
        this.subtaskDescription = description;
    }

    public SubTasks() {
    }

    @PrePersist
    private void prePersist() {
        this.status = EventStatus.TODO;
        this.priority = PriorityStatus.NONE;
    }
}
