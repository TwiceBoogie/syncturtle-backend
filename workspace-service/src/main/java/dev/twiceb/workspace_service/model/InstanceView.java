package dev.twiceb.workspace_service.model;

import java.time.Instant;
import java.util.UUID;
import dev.twiceb.common.enums.InstanceEdition;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "instances_view")
public class InstanceView {

    @Id
    private UUID id;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(name = "edition", nullable = false)
    private InstanceEdition edition;

    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
