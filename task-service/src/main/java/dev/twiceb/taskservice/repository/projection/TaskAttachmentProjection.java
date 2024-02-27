package dev.twiceb.taskservice.repository.projection;

import java.time.LocalDateTime;

public interface TaskAttachmentProjection {
    Long getId();
    Long getFileName();
    String getFileType();
    LocalDateTime getUploadDate();
}
