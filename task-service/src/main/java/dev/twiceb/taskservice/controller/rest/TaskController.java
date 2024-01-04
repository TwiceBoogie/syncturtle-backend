package dev.twiceb.taskservice.controller.rest;

import dev.twiceb.common.dto.request.NewRecurringEventRequest;
import dev.twiceb.common.dto.response.GenericResponse;
import dev.twiceb.common.dto.response.HeaderResponse;
import dev.twiceb.taskservice.dto.request.NewTaskRequest;
import dev.twiceb.taskservice.dto.response.RecurringTasksResponse;
import dev.twiceb.taskservice.mapper.TaskMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import static dev.twiceb.common.constants.PathConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(UI_V1_DASHBOARD)
public class TaskController {

    private final TaskMapper taskMapper;

    @PostMapping(CREATE_TASK)
    public ResponseEntity<GenericResponse> createNewTask(
            @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
            @Valid @RequestBody NewTaskRequest request,
            BindingResult bindingResult
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.createNewTask(
                userId, request, bindingResult));
    }

    @PostMapping(CREATE_RECURRING_TASK)
    public ResponseEntity<GenericResponse> createRecurringTask(
            @RequestHeader(name = AUTH_USER_ID_HEADER, defaultValue = "0") Long userId,
            @Valid @RequestBody NewRecurringEventRequest request,
            BindingResult bindingResult
            ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.createNewRecurringTask(
                userId, request, bindingResult));
    }
}
