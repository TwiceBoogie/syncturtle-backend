package dev.twiceb.taskservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "workspaces")
public class Workspace extends BaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "logo")
    private String logo;

    @Column(name = "slug")
    private String slug;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
