package dev.twiceb.taskservice.repository.projection;

import dev.twiceb.taskservice.model.Task;

public interface AttachmentKeyProjection {
    Task getTask();
    String getFilePath();
    String getFileType();
}
