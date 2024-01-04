package dev.twiceb.taskservice.repository;

import dev.twiceb.taskservice.model.SubTasks;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtasksRepository extends JpaRepository<SubTasks, Long> {
}
