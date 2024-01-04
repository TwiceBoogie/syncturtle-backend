package dev.twiceb.taskservice.model.statistics;

import dev.twiceb.taskservice.model.Accounts;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "user_tasks_progress")
public class UserTaskProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Accounts account;

    @Column(name = "completed_tasks", nullable = false)
    private int completedTasks;

    @Column(name = "total_tasks", nullable = false)
    private int totalTasks;

    @Column(name = "completion_percentage", precision = 5, scale = 2)
    private BigDecimal completionPercentage;

    @Column(name = "created_date", nullable = false)
    private ZonedDateTime createdDate;

    public UserTaskProgress() {}

    public UserTaskProgress(Accounts account, int completedTasks, int totalTasks, BigDecimal completionPercentage) {
        this.account = account;
        this.completedTasks = completedTasks;
        this.totalTasks = totalTasks;
        this.completionPercentage = completionPercentage;
    }
}
