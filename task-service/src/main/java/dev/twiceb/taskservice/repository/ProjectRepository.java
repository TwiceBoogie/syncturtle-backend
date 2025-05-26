package dev.twiceb.taskservice.repository;

import dev.twiceb.taskservice.model.Project;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;

public interface ProjectRepository extends JpaAttributeConverter<Project, Long> {
}
