package dev.twiceb.taskservice.service.impl;

import dev.twiceb.common.dto.request.FileImageRequest;
import dev.twiceb.common.dto.request.NewRecurringEventRequest;
import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.model.Tags;
import dev.twiceb.common.repository.TagsRepository;
import dev.twiceb.common.util.AuthUtil;
import dev.twiceb.common.util.UpdateQueryResult;
import dev.twiceb.taskservice.dto.request.*;
import dev.twiceb.taskservice.feign.FileClient;
import dev.twiceb.taskservice.model.*;
import dev.twiceb.taskservice.repository.*;
import dev.twiceb.taskservice.repository.projection.AttachmentKeyProjection;
import dev.twiceb.taskservice.repository.projection.RecurringTaskProjection;
import dev.twiceb.taskservice.repository.projection.SubtaskProjection;
import dev.twiceb.taskservice.repository.projection.TaskProjection;
import dev.twiceb.taskservice.service.TaskService;
import dev.twiceb.taskservice.service.UserService;
import dev.twiceb.taskservice.service.util.TaskServiceHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dev.twiceb.common.constants.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private static final String TASK_ATTACHMENT_FOLDER = "task_attachment";

    private final TasksRepository tasksRepository;
    private final UserRepository userRepository;
    private final RecurringTasksRepository recurringTasksRepository;
    private final TaskServiceHelper taskServiceHelper;
    private final TagsRepository tagsRepository;
    private final TaskAttachmentRepository taskAttachmentRepository;
    private final FileClient fileClient;
    private final UserService userService;

    @Override
    public Map<String, String> createNewProject(NewProjectRequest request, BindingResult bindingResult) {
        return Map.of();
    }

    @Override
    @Transactional
    public Map<String, String> createNewTask(NewTaskRequest request, BindingResult bindingResult) {
        taskServiceHelper.processBindingResults(bindingResult);
        User authUser = userService.getAuthUser();
        Task task = new Task();

        if (!request.getSubtasks().isEmpty()) {
            task.setSubtasks(handleSubtasks(request.getSubtasks(), task));
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
    public Map<String, String> uploadAttachments(UUID taskId, MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new ApiRequestException("No files provided for upload.", HttpStatus.BAD_REQUEST);
        }
        List<String> imageUrls = fileClient.uploadImages(files, TASK_ATTACHMENT_FOLDER);
        Task task = tasksRepository.findById(taskId).orElseThrow(
                () -> new ApiRequestException(NO_TASK_FOUND, HttpStatus.NOT_FOUND));
        UUID authUserId = AuthUtil.getAuthenticatedUserId();
        if (!task.getUser().getId().equals(authUserId)) {
            throw new ApiRequestException(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }

        List<TaskAttachment> attachments = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++) {
            MultipartFile file = files[i];
            String imageUrl = imageUrls.get(i);
            TaskAttachment attachment = new TaskAttachment(
                    task, file.getOriginalFilename(), imageUrl, file.getContentType());
            attachments.add(attachment);
        }

        task.getAttachments().addAll(attachments);
        tasksRepository.save(task);

        return Map.of("message", "Attachments successfully uploaded.");
    }

    @Override
    public Map<String, Object> getFileImage(Long taskAttachmentId) {
        AttachmentKeyProjection key = taskAttachmentRepository.getAttachmentKey(taskAttachmentId);
        User user = key.getTask().getUser();
        UUID authUserId = AuthUtil.getAuthenticatedUserId();
        if (!user.getId().equals(authUserId)) {
            throw new ApiRequestException(UNAUTHORIZED, HttpStatus.UNAUTHORIZED);
        }
        FileImageRequest request = new FileImageRequest();
        request.setImageUrl(key.getFilePath());
        byte[] content = fileClient.getFileImage(request);
        return Map.of("photoContent", content, "fileType", key.getFileType());
    }

    @Override
    @Transactional
    public Map<String, String> createNewRecurringTask(NewRecurringEventRequest request,
            BindingResult bindingResult) {
        UUID authUserId = AuthUtil.getAuthenticatedUserId();
        taskServiceHelper.processBindingResults(bindingResult);

        if (recurringTasksRepository.isRecurringTaskExist(authUserId, request.getTitle())) {
            throw new ApiRequestException(DUPLICATE_RECURRENCE_TASK, HttpStatus.BAD_REQUEST);
        }

        RecurringTask recurringTask = new RecurringTask(
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
    public Map<String, String> addSubtaskToTask(Long taskId, NewSubTaskRequest request,
            BindingResult bindingResult) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        Task taskFromDB = tasksRepository.findTaskByUserAndTaskId(authUserId, taskId, Task.class)
                .orElseThrow(() -> new ApiRequestException(NO_TASK_FOUND, HttpStatus.NOT_FOUND));

        if (taskFromDB.getSubtasks().size() >= 10) {
            throw new ApiRequestException(EXCEED_SUBGOAL_SIZE, HttpStatus.BAD_REQUEST);
        }

        SubTask subtask = new SubTask(taskFromDB, request.getName(), request.getDescription());
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
    public Map<String, String> updateTask(UpdateTaskRequest request, BindingResult bindingResult) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        Task taskFromDb = tasksRepository
                .findTaskByUserAndTaskId(authUserId, request.getId(), Task.class)
                .orElseThrow(() -> new ApiRequestException(NO_TASK_FOUND, HttpStatus.NOT_FOUND));

        UpdateQueryResult result = taskServiceHelper.buildQuery(request, "tasks", "id");
        taskServiceHelper.executeQuery(result, "id");

        return Map.of("message", "Task updated successfully");
    }

    @Override
    public Map<String, String> updateSubTask(UpdateSubtaskRequest request, BindingResult bindingResult) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskProjection> getTasks(Pageable pageable) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return tasksRepository.getTasksByUserId(authUserId, pageable, TaskProjection.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubtaskProjection> getSubtasks(Long taskId) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        return tasksRepository.findSubtasksByTaskId(taskId, authUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RecurringTaskProjection> getRecurringTask(Pageable pageable) {
        Long authUserId = AuthUtil.getAuthenticatedUserId();
        Page<RecurringTaskProjection> recurringTasks = recurringTasksRepository.findRecurringTasksByUserId(
                authUserId,
                pageable,
                RecurringTaskProjection.class);
        if (!recurringTasks.hasContent()) {
            throw new ApiRequestException("No content found.", HttpStatus.NO_CONTENT);
        }

        return recurringTasks;
    }

    private List<SubTask> handleSubtasks(List<NewSubTaskRequest> subTasks, Task task) {
        List<SubTask> newSubtaskList = new ArrayList<>();
        for (NewSubTaskRequest subTask : subTasks) {
            SubTask newSubTask = new SubTask(task, subTask.getName(), subTask.getDescription());

            if (subTask.getPriority() != null) {
                newSubTask.setPriority(subTask.getPriority());
            }
            if (subTask.getStatus() != null) {
                newSubTask.setStatus(subTask.getStatus());
            }

            newSubtaskList.add(newSubTask);
        }
        return newSubtaskList;
    }
}
