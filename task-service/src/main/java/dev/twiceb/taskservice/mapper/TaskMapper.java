package dev.twiceb.taskservice.mapper;

import dev.twiceb.common.dto.request.NewRecurringEventRequest;
import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.common.mapper.BasicMapper;
import dev.twiceb.taskservice.dto.request.NewTaskRequest;
import dev.twiceb.taskservice.dto.response.RecurringTasksResponse;
import dev.twiceb.taskservice.dto.response.SubtasksResponse;
import dev.twiceb.taskservice.dto.response.TasksResponse;
import dev.twiceb.taskservice.repository.projection.TaskProjection;
import dev.twiceb.taskservice.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskMapper {

    private final BasicMapper basicMapper;
    private final TaskService taskService;

    public GenericResponse createNewTask(Long userId, NewTaskRequest request, BindingResult bindingResult) {
        return basicMapper.convertToResponse(taskService.createNewTask(userId, request, bindingResult), GenericResponse.class);
    }

    public GenericResponse createNewRecurringTask(Long userId, NewRecurringEventRequest request, BindingResult bindingResult) {
        return basicMapper.convertToResponse(
                taskService.createNewRecurringTask(userId, request, bindingResult), GenericResponse.class
        );
    }

    public HeaderResponse<TasksResponse> getTasks(Long userId, Pageable pageable) {
        Page<TaskProjection> tasks = taskService.getTasks(userId, pageable);
        return basicMapper.getHeaderResponse(tasks, TasksResponse.class);
    }

    public List<SubtasksResponse> getSubtasks(Long userId, Long taskId) {
        return basicMapper.convertToResponseList(taskService.getSubtasks(userId, taskId), SubtasksResponse.class);
    }

    public HeaderResponse<RecurringTasksResponse> getRecurringTasks(Long userId, Pageable pageable) {
        return basicMapper.getHeaderResponse(taskService.getRecurringTask(userId, pageable), RecurringTasksResponse.class);
    }
}
