package dev.twiceb.taskservice.model;

import dev.twiceb.common.enums.EventStatus;
import dev.twiceb.common.enums.PriorityStatus;
import dev.twiceb.common.model.Tags;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "tasks")
public class Task extends ProjectBaseModel {

        @Column(name = "name", nullable = false)
        private String name;

        @Column(name = "description", nullable = false)
        private String description;

        @Column(name = "start_date")
        private LocalDateTime startDate;

        @Column(name = "target_date")
        private LocalDateTime targetDate;

        @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
        @JoinColumn(name = "parent_id")
        private Task parent;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "module_id")
        private Module module;

        @Column(name = "completed_at")
        private LocalDateTime completedAt;

        @Column(name = "priority", nullable = false)
        @ColumnTransformer(read = "priority", write = "?::priority_status")
        private PriorityStatus priority = PriorityStatus.NONE;

        @Column(name = "status", nullable = false)
        @ColumnTransformer(read = "status", write = "?::task_status")
        private EventStatus taskStatus = EventStatus.IN_PROGRESS;

        @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<TaskAttachment> attachments = new ArrayList<>();

        @ManyToMany
        @JoinTable(name = "task_tags", joinColumns = { @JoinColumn(name = "task_id") }, inverseJoinColumns = {
                        @JoinColumn(name = "tag_id") })
        private List<Tags> tags = new ArrayList<>();

        public Task() {
        }

        public Task(String name, String description) {
                this.name = name;
                this.description = description;
        }
}
