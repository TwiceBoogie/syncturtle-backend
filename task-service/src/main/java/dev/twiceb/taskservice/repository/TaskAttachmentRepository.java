package dev.twiceb.taskservice.repository;

import dev.twiceb.taskservice.model.TaskAttachment;
import dev.twiceb.taskservice.repository.projection.AttachmentKeyProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskAttachmentRepository extends JpaRepository<TaskAttachment, Long> {

    @Query("SELECT ta FROM TaskAttachment ta WHERE ta.id = :attachmentId")
    AttachmentKeyProjection getAttachmentKey(@Param("attachmentId") Long attachmentId);
}
