package dev.twiceb.taskservice.repository.statistics;

import dev.twiceb.taskservice.model.statistics.UserTaskProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTasksProgressRepository extends JpaRepository<UserTaskProgress, Long> {
}
