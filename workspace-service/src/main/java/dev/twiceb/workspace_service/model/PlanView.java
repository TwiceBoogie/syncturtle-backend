package dev.twiceb.workspace_service.model;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.NaturalId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "plans_view")
public class PlanView {

    @Id
    private UUID id;

    @NaturalId
    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
