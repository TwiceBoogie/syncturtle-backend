package dev.twiceb.passwordservice.service.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import dev.twiceb.passwordservice.enums.DomainStatus;

@Converter(autoApply = true)
public class DomainStatusConverter implements AttributeConverter<DomainStatus, String> {

    @Override
    public String convertToDatabaseColumn(DomainStatus attribute) {
        return attribute.toString();
    }

    @Override
    public DomainStatus convertToEntityAttribute(String dbData) {
        return DomainStatus.valueOf(dbData);
    }
}
