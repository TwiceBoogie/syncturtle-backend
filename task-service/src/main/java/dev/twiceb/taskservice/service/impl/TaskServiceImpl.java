package dev.twiceb.taskservice.service.impl;

import dev.twiceb.common.dto.request.NewRecurringEventRequest;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.model.Tags;
import dev.twiceb.common.repository.TagsRepository;
import dev.twiceb.common.util.UpdateQueryResult;
import dev.twiceb.taskservice.dto.request.NewSubTaskRequest;
import dev.twiceb.taskservice.dto.request.NewTaskRequest;
import dev.twiceb.taskservice.dto.request.UpdateSubtaskRequest;
import dev.twiceb.taskservice.dto.request.UpdateTaskRequest;
import dev.twiceb.taskservice.model.Accounts;
import dev.twiceb.taskservice.model.RecurringTasks;
import dev.twiceb.taskservice.model.SubTasks;
import dev.twiceb.taskservice.model.Tasks;
import dev.twiceb.taskservice.repository.AccountsRepository;
import dev.twiceb.taskservice.repository.RecurringTasksRepository;
import dev.twiceb.taskservice.repository.SubtasksRepository;
import dev.twiceb.taskservice.repository.TasksRepository;
import dev.twiceb.taskservice.repository.projection.RecurringTaskProjection;
import dev.twiceb.taskservice.repository.projection.SubtaskProjection;
import dev.twiceb.taskservice.repository.projection.TaskProjection;
import dev.twiceb.taskservice.service.TaskService;
import dev.twiceb.taskservice.service.util.TaskServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TasksRepository tasksRepository;
    private final SubtasksRepository subtasksRepository;
    private final AccountsRepository accountsRepository;
    private final RecurringTasksRepository recurringTasksRepository;
    private final TaskServiceHelper taskServiceHelper;
    private final TagsRepository tagsRepository;

    @Override
    @Transactional
    public Map<String, String> createNewTask(Long userId, NewTaskRequest request, BindingResult bindingResult) {
        Accounts userAccount = getUserAccount(userId);
        taskServiceHelper.processBindingResults(bindingResult);

        Tasks task = new Tasks(userAccount, request.getTaskTitle(), request.getTaskDescriptions(),
                request.getDueDate());
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            task.setTaskStatus(request.getStatus());
        }

        if (!request.getSubtasks().isEmpty()) {
            task.setSubtasks(taskServiceHelper.handleSubtasks(request.getSubtasks(), task));
        }

        if (!request.getTags().getTagNames().isEmpty()) {
            List<String> tagNamesListVer = new ArrayList<>(request.getTags().getTagNames());
            List<Integer> existingTagsResults = tagsRepository.checkExistingTags(tagNamesListVer);
            List<Tags> tags = taskServiceHelper.handleTags(existingTagsResults, tagNamesListVer);
            task.setTags(tagsRepository.saveAll(tags));
        }
        tasksRepository.save(task);
        // TODO: instead of sending a message, return the saved task for immediate
        // feedback and no reload to fetch
        // TODO: send data to userStatsService to aggregate data
        return Map.of("message", "New task created successfully");
    }

    @Override
    @Transactional
    public Map<String, String> createNewRecurringTask(Long userId, NewRecurringEventRequest request,
            BindingResult bindingResult) {
        Accounts userAccount = getUserAccount(userId);
        taskServiceHelper.processBindingResults(bindingResult);

        if (recurringTasksRepository.isRecurringTaskExist(userAccount.getUserId(), request.getTitle())) {
            throw new ApiRequestException(DUPLICATE_RECURRENCE_TASK, HttpStatus.BAD_REQUEST);
        }

        RecurringTasks recurringTask = new RecurringTasks(
                userAccount,
                request.getTitle(),
                request.getDescription(),
                request.getRecurrencePattern());
        if (request.getRecurrenceFreq() != null) {
            recurringTask.setRecurrenceFreq(request.getRecurrenceFreq());
        }
        if (request.getRecurrenceEndDate() != null) {
            recurringTask.setRecurrenceEndDate(request.getRecurrenceEndDate());
        }

        recurringTasksRepository.save(recurringTask);

        return Map.of("message", "New recurring task created successfully.");
    }

    @Override
    @Transactional
    public Map<String, String> addSubtaskToTask(Long userId, Long taskId, NewSubTaskRequest request,
            BindingResult bindingResult) {
        Accounts userAccount = getUserAccount(userId);
        Tasks taskFromDB = tasksRepository.findTaskByUserAndTaskId(userAccount.getUserId(), taskId, Tasks.class)
                .orElseThrow(
                        () -> new ApiRequestException(NO_TASK_FOUND, HttpStatus.NOT_FOUND));

        if (taskFromDB.getSubtasks().size() >= 10) {
            throw new ApiRequestException(EXCEED_SUBGOAL_SIZE, HttpStatus.BAD_REQUEST);
        }

        SubTasks subtask = new SubTasks(taskFromDB, request.getSubtaskTitle(), request.getSubtaskDescription());
        if (request.getStatus() != null) {
            subtask.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            subtask.setPriority(request.getPriority());
        }

        taskFromDB.getSubtasks().add(subtask);
        tasksRepository.save(taskFromDB);

        return Map.of("message", "New subtask added successfully.");
    }

    @Override
    public Map<String, String> updateTask(Long userId, UpdateTaskRequest request, BindingResult bindingResult) {
        Accounts userAccount = getUserAccount(userId);
        Tasks taskFromDb = tasksRepository
                .findTaskByUserAndTaskId(userAccount.getUserId(), request.getId(), Tasks.class)
                .orElseThrow(() -> new ApiRequestException(NO_TASK_FOUND, HttpStatus.NOT_FOUND));

        UpdateQueryResult result = taskServiceHelper.buildQuery(request, "tasks", "id");
        taskServiceHelper.executeQuery(result, "id");

        return Map.of("message", "Task updated successfully");
    }

    @Override
    public Map<String, String> updateSubTask(Long userId, UpdateSubtaskRequest request, BindingResult bindingResult) {
        return null;
    }

    @Override
    public Page<TaskProjection> getTasks(Long userId, Pageable pageable) {
        Accounts userAccount = getUserAccount(userId);
        return tasksRepository.getTasksByUserId(
                userAccount.getUserId(), pageable, TaskProjection.class);
    }

    @Override
    public List<SubtaskProjection> getSubtasks(Long userId, Long taskId) {
        Accounts userAccount = getUserAccount(userId);
        return tasksRepository.findSubtasksByTaskId(taskId, userAccount.getUserId());
    }

    @Override
    public Page<RecurringTaskProjection> getRecurringTask(Long userId, Pageable pageable) {
        Accounts userAccount = getUserAccount(userId);
        Page<RecurringTaskProjection> recurringTasks = recurringTasksRepository.findRecurringTasksByUserId(
                userAccount.getUserId(),
                pageable,
                RecurringTaskProjection.class);
        if (!recurringTasks.hasContent()) {
            throw new ApiRequestException("No content found.", HttpStatus.NO_CONTENT);
        }

        return recurringTasks;
    }

    private Accounts getUserAccount(Long userId) {
        return accountsRepository.findAccountByUserId(userId, Accounts.class).orElseThrow(
                () -> new ApiRequestException(AUTHENTICATION_ERROR, HttpStatus.UNAUTHORIZED));
    }
}
