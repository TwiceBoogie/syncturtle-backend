package dev.twiceb.taskservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class ProjectBaseModel extends BaseModel {

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;
}
