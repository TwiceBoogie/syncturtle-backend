package dev.twiceb.taskservice.service.util;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.model.Tags;
import dev.twiceb.common.util.ServiceHelper;
import dev.twiceb.common.util.UpdateQueryResult;
import dev.twiceb.taskservice.dto.request.NewSubTaskRequest;
import dev.twiceb.taskservice.model.SubTasks;
import dev.twiceb.taskservice.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.text.ParseException;
import java.util.*;

import static dev.twiceb.common.constants.ErrorMessage.INTERNAL_SERVER_ERROR;

@Component
@RequiredArgsConstructor
public class TaskServiceHelper extends ServiceHelper {

    @PersistenceContext
    private final EntityManager entityManager;

    public void processBindingResults(BindingResult bindingResult) {
        this.processInputErrors(bindingResult);
    }

    public List<SubTasks> handleSubtasks(List<NewSubTaskRequest> subTasks, Task task) {
        List<SubTasks> newSubtaskList = new ArrayList<>();
        for (NewSubTaskRequest subTask : subTasks) {
            SubTasks newSubTask = new SubTasks(task, subTask.getSubtaskTitle(), subTask.getSubtaskDescription());

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

    public List<Tags> handleTags(List<Integer> existingTagsResult, List<String> tagsRequest) {
        List<Tags> tagsList = new ArrayList<>();
        int index = 0;
        for (Integer tagResult : existingTagsResult) {
            if (tagResult == 0) {
                Tags tag = new Tags(tagsRequest.get(index));
                tagsList.add(tag);
            }
            index++;
        }
        return tagsList;
    }

    public UpdateQueryResult buildQuery(Object entity, String tableName, String identifierColumn) {
        try {
            return this.buildUpdateQuery(entity, tableName, identifierColumn);
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void executeQuery(UpdateQueryResult result, String identifierColumn) {
        try {
            this.executeUpdateQuery(result, identifierColumn);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
