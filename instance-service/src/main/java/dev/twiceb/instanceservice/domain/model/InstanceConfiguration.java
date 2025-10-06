package dev.twiceb.instanceservice.domain.model;

import org.hibernate.annotations.NaturalId;
import dev.twiceb.common.enums.InstanceConfigurationKey;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "instance_configurations")
public class InstanceConfiguration extends AuditableEntity {

    @NaturalId
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
