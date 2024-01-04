package dev.twiceb.taskservice.service.util;

import dev.twiceb.common.exception.ApiRequestException;
import dev.twiceb.common.model.Tags;
import dev.twiceb.common.util.ServiceHelper;
import dev.twiceb.taskservice.dto.request.NewSubTaskRequest;
import dev.twiceb.taskservice.dto.request.TagsRequest;
import dev.twiceb.taskservice.dto.request.UpdateTaskRequest;
import dev.twiceb.taskservice.model.SubTasks;
import dev.twiceb.taskservice.model.Tasks;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static dev.twiceb.common.constants.ErrorMessage.INTERNAL_SERVER_ERROR;

@Component
public class TaskServiceHelper extends ServiceHelper {

    public List<SubTasks> handleSubtasks(List<NewSubTaskRequest> subTasks, Tasks task) {
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

    public String buildQuery(Object entity, String tableName, String identifierColumn) {
        try {
            return this.buildUpdateQuery(entity, tableName, identifierColumn);
        } catch (IllegalAccessException e) {
            System.out.println(e.getMessage());
            throw new ApiRequestException(INTERNAL_SERVER_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
