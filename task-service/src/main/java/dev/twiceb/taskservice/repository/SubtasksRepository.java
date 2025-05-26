package dev.twiceb.taskservice.repository;

import dev.twiceb.taskservice.model.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubtasksRepository extends JpaRepository<SubTask, Long> {
}
