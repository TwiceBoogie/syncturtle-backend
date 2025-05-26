package dev.twiceb.taskservice.service;

import dev.twiceb.common.dto.request.NewRecurringEventRequest;
import dev.twiceb.taskservice.dto.request.*;
import dev.twiceb.taskservice.repository.projection.RecurringTaskProjection;
import dev.twiceb.taskservice.repository.projection.SubtaskProjection;
import dev.twiceb.taskservice.repository.projection.TaskProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TaskService {
    Map<String, String> createNewProject(NewProjectRequest request, BindingResult bindingResult);
    Map<String, String> createNewTask(NewTaskRequest request, BindingResult bindingResult);
    Map<String, String> uploadAttachments(UUID taskId, MultipartFile[] files);
    Map<String, Object> getFileImage(UUID taskAttachmentId);
    Map<String, String> createNewRecurringTask(NewRecurringEventRequest request, BindingResult bindingResult);
    Map<String, String> addSubtaskToTask(UUID taskId, NewSubTaskRequest request, BindingResult bindingResult);
    Map<String, String> updateTask(UpdateTaskRequest request, BindingResult bindingResult);
    Map<String, String> updateSubTask(UpdateSubtaskRequest request, BindingResult bindingResult);
    Page<TaskProjection> getTasks(Pageable pageable);
    List<SubtaskProjection> getSubtasks(UUID taskId);
    Page<RecurringTaskProjection> getRecurringTask(Pageable pageable);

}
