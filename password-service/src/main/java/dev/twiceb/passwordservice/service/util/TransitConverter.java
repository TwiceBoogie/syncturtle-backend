package dev.twiceb.passwordservice.service.util;

import dev.twiceb.passwordservice.model.support.CryptoHolder;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Converter(autoApply = false)
public class TransitConverter implements AttributeConverter<String, String> {

    private static final String KEY_NAME = "password";

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null)
            return null;
        String ct = CryptoHolder.get().encrypt(KEY_NAME, attribute);
        log.debug("Encrypted with transit, len={}", ct.length());
        return ct;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return CryptoHolder.get().encrypt(KEY_NAME, dbData);
    }
}
