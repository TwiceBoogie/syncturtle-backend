package dev.twiceb.taskservice.controller.api;

import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.taskservice.dto.response.RecurringTasksResponse;
import dev.twiceb.taskservice.dto.response.SubtasksResponse;
import dev.twiceb.taskservice.dto.response.TasksResponse;
import dev.twiceb.taskservice.mapper.TaskMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(API_V1_DASHBOARD)
public class TaskApiController {

    private final TaskMapper taskMapper;

    @GetMapping(GET_TASKS)
    public HeaderResponse<TasksResponse> getTasks(
            @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
            Pageable pageable
    ) {
        return taskMapper.getTasks(userId, pageable);
    }

    @GetMapping(GET_SUBTASKS_FOR_TASK)
    public List<SubtasksResponse> getSubtasksForTask(
            @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
            @PathVariable("taskId") Long taskId
    ) {
        return taskMapper.getSubtasks(userId, taskId);
    }

    @GetMapping(GET_RECURRING_TASKS)
    public HeaderResponse<RecurringTasksResponse> getRecurringTask(
            @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
            Pageable pageable
    ) {
        return taskMapper.getRecurringTasks(userId, pageable);
    }
}
