package dev.twiceb.instanceservice.domain.model;

import java.util.UUID;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "instance_configurations")
@Getter
@Setter
public class InstanceConfiguration extends AuditableEntity {

    @Id
    private UUID id = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "key", nullable = false, unique = true)
    private InstanceConfigurationKey key;

    @Column(name = "value")
    private String value;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "is_encrypted", nullable = false)
    private boolean encrypted;
}
