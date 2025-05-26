package dev.twiceb.taskservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class AuditModel {

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "updated_by_id")
    private User updatedBy;

}
