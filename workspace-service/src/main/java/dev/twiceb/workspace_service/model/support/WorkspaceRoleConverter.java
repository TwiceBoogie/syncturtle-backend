package dev.twiceb.workspace_service.model.support;

import dev.twiceb.workspace_service.enums.WorkspaceRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class WorkspaceRoleConverter implements AttributeConverter<WorkspaceRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(WorkspaceRole attribute) {
        return attribute == null ? null : attribute.code;
    }

    @Override
    public WorkspaceRole convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : WorkspaceRole.from(dbData);
    }

}
