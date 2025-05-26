package dev.twiceb.taskservice.repository;

import dev.twiceb.taskservice.model.Task;
import dev.twiceb.taskservice.repository.projection.SubtaskProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TasksRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE t.account.userId = :userId ORDER BY t.targetDate ASC")
    <T> Page<T> getTasksByUserId(@Param("userId") UUID userId, Pageable pageable, Class<T> clazz);

    @Query("SELECT st FROM Task t JOIN t.subtasks st WHERE t.id = :taskId AND t.account.userId = :userId")
    List<SubtaskProjection> findSubtasksByTaskId(@Param("taskId") UUID taskId, @Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.account.userId = :userId AND t.id = :taskId")
    <T> Optional<T> findTaskByUserAndTaskId(@Param("userId") UUID userId, @Param("taskId") Long taskId, Class<T> clazz);


}
