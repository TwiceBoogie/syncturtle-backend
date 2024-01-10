package dev.twiceb.taskservice.service;

import dev.twiceb.common.dto.request.NewRecurringEventRequest;
import dev.twiceb.taskservice.dto.request.NewSubTaskRequest;
import dev.twiceb.taskservice.dto.request.NewTaskRequest;
import dev.twiceb.taskservice.dto.request.UpdateSubtaskRequest;
import dev.twiceb.taskservice.dto.request.UpdateTaskRequest;
import dev.twiceb.taskservice.repository.projection.RecurringTaskProjection;
import dev.twiceb.taskservice.repository.projection.SubtaskProjection;
import dev.twiceb.taskservice.repository.projection.TaskProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;

import java.util.List;
import java.util.Map;

public interface TaskService {

    public Map<String, String> createNewTask(Long userId, NewTaskRequest request, BindingResult bindingResult);

    public Map<String, String> createNewRecurringTask(Long userId, NewRecurringEventRequest request, BindingResult bindingResult);

    public Map<String, String> addSubtaskToTask(Long userId, Long taskId, NewSubTaskRequest request, BindingResult bindingResult);

    public Map<String, String> updateTask(Long userId, UpdateTaskRequest request, BindingResult bindingResult);

    public Map<String, String> updateSubTask(Long userId, UpdateSubtaskRequest request, BindingResult bindingResult);

    public Page<TaskProjection> getTasks(Long userId, Pageable pageable);

    public List<SubtaskProjection> getSubtasks(Long userId, Long taskId);

    public Page<RecurringTaskProjection> getRecurringTask(Long userId, Pageable pageable);

}
