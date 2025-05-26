package dev.twiceb.taskservice.model;

import dev.twiceb.common.enums.EventStatus;
import dev.twiceb.common.enums.PriorityStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "subtasks")
public class SubTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "target_date", nullable = false)
    private LocalDateTime targetDate;

    @Column(name = "status", nullable = false)
    private EventStatus status;

    @Column(name = "priority", nullable = false)
    private PriorityStatus priority;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public SubTask(Task task, String title, String description) {
        this.task = task;
        this.name = title;
        this.description = description;
    }

    public SubTask() {
    }

    @PrePersist
    private void prePersist() {
        this.status = EventStatus.TODO;
        this.priority = PriorityStatus.NONE;
    }
}
