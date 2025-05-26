package dev.twiceb.taskservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "modules", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "project_id"})})
public class Module extends ProjectBaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "target_date")
    private LocalDateTime targetDate;

    @Column(name = "status", nullable = false)
    private String status;

}
