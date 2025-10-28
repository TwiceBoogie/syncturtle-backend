package dev.twiceb.userservice.domain.model.support;

import dev.twiceb.common.enums.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<UserRole, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserRole attribute) {
        return attribute == null ? null : attribute.code;
    }

    @Override
    public UserRole convertToEntityAttribute(Integer dbData) {
        return dbData == null ? null : UserRole.from(dbData);
    }

}
