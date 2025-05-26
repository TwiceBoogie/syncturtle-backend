package dev.twiceb.taskservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "module_issues", uniqueConstraints = {@UniqueConstraint(columnNames = {"module", "task"})})
public class ModuleIssue extends ProjectBaseModel {

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
