package dev.twiceb.taskservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "projects")
public class Project extends BaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "target_date")
    private LocalDateTime targetDate;

    @Column(name = "cover_image")
    private String coverImage;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

}
