package dev.twiceb.taskservice.repository;

import dev.twiceb.taskservice.model.RecurringTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecurringTasksRepository extends JpaRepository<RecurringTask, Long> {

    @Query("SELECT CASE WHEN COUNT(rt) > 0 THEN TRUE ELSE FALSE END " +
            "FROM RecurringTask rt WHERE rt.account.userId = :userId " +
            "AND LOWER(rt.taskTitle) = :taskTitle")
    boolean isRecurringTaskExist(@Param("userId") Long userId, @Param("taskTitle") String taskTitle);

    @Query("SELECT rt FROM RecurringTask rt WHERE rt.account.userId = :userId")
    <T> Page<T> findRecurringTasksByUserId(@Param("userId") Long userId, Pageable pageable, Class<T> clazz);
}
