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
import java.util.Set;

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

    @OneToMany(mappedBy = "tasks", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubTasks> subtasks;

    @ManyToMany
    @JoinTable(
            name = "task_tags",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tags> tags;

    private Tasks(Builder builder) {
        this.account = builder.account;
        this.taskTitle = builder.taskTitle;
        this.taskDescription = builder.taskDescription;
        this.dueDate = builder.dueDate;
        this.priority = builder.priority;
        this.taskStatus = builder.status;
        this.subtasks = builder.subtasks;
        this.tags = builder.tags;
    }

    protected Tasks() {
    }

    public static class Builder {
        private final Accounts account;
        private final String taskTitle;
        private final String taskDescription;
        private final Date dueDate;
        private PriorityStatus priority;
        private TaskStatus status;
        private List<SubTasks> subtasks;
        private Set<Tags> tags;

        public Builder(Accounts account, String taskTitle, String taskDescription, Date dueDate) {
            if (account == null || taskTitle == null || taskDescription == null || dueDate == null) {
                throw new IllegalArgumentException("User account must be present");
            }
            this.account = account;
            this.taskTitle = taskTitle;
            this.taskDescription = taskDescription;
            this.dueDate = dueDate;
        }

        public Builder withSubtasks(List<SubTasks> subtasks) {
            this.subtasks = subtasks;
            return this;
        }

        public Builder withTags(Set<Tags> tags) {
            this.tags = tags;
            return this;
        }

        public Builder withPriority(PriorityStatus priority) {
            this.priority = priority;
            return this;
        }

        public Builder withTaskStatus(TaskStatus status) {
            this.status = status;
            return this;
        }

        public Tasks build() { return new Tasks(this);}
    }
}
