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
@RequestMapping(API_V1_TASK)
public class TaskApiController {

    private final TaskMapper taskMapper;

    @GetMapping
    public HeaderResponse<TasksResponse> getTasks(Pageable pageable) {
        return taskMapper.getTasks(pageable);
    }

    @GetMapping(GET_SUBTASKS_FOR_TASK)
    public List<SubtasksResponse> getSubtasksForTask(@PathVariable("taskId") Long taskId) {
        return taskMapper.getSubtasks(taskId);
    }

    @GetMapping(GET_RECURRING_TASKS)
    public HeaderResponse<RecurringTasksResponse> getRecurringTasks(Pageable pageable) {
        return taskMapper.getRecurringTasks(pageable);
    }
}
