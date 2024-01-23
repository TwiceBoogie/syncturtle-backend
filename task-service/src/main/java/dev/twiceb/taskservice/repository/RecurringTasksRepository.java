package dev.twiceb.taskservice.repository;

import dev.twiceb.taskservice.model.RecurringTasks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecurringTasksRepository extends JpaRepository<RecurringTasks, Long> {

    @Query("SELECT CASE WHEN COUNT(rt) > 0 THEN TRUE ELSE FALSE END " +
            "FROM RecurringTasks rt WHERE rt.account.userId = :userId " +
            "AND LOWER(rt.taskTitle) = :taskTitle")
    boolean isRecurringTaskExist(@Param("userId") Long userId, @Param("taskTitle") String taskTitle);

    @Query("SELECT rt FROM RecurringTasks rt WHERE rt.account.userId = :userId")
    <T> Page<T> findRecurringTasksByUserId(@Param("userId") Long userId, Pageable pageable, Class<T> clazz);
}
