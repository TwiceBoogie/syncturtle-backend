package dev.twiceb.instance_service.model;

import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "instance_configurations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"instance_id", "key"}))
@Getter
@Setter
public class InstanceConfiguration extends AuditableEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @Column(name = "key", nullable = false)
    private String key;

    @Column(name = "value")
    private String value;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "is_encrypted", nullable = false)
    private boolean encrypted;
}
