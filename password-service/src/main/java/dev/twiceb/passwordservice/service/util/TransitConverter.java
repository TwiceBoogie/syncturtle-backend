package dev.twiceb.passwordservice.service.util;

import dev.twiceb.common.util.BeanUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Plaintext;

import java.util.Base64;

@Slf4j
@Converter
public class TransitConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        log.info("method called from convertToDatabaseColumn(): String to save -> {}", attribute);
        VaultOperations vaultOps = BeanUtil.getBean(VaultOperations.class);
        Plaintext plaintext = Plaintext.of(attribute);
        String encrypted = vaultOps.opsForTransit().encrypt("password", plaintext).getCiphertext();
        log.info("converted to entity attribute: plaintext -> {}, Encrypted -> {}", attribute, encrypted);
        return encrypted;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        log.info("method called from convertToEntityAttribute(): String from db -> {}", dbData);
        VaultOperations vaultOps = BeanUtil.getBean(VaultOperations.class);
        Ciphertext ciphertext = Ciphertext.of(dbData);
        return vaultOps.opsForTransit().decrypt("password", ciphertext).asString();
//        byte[] decryptedBytes = vaultOps.opsForTransit().decrypt("password", ciphertext).getPlaintext();
//        String decrypted = Base64.getEncoder().encodeToString(decryptedBytes);
//        log.info("Converted to entity attribute: Encrypted -> " + dbData + ", Decrypted -> " + decrypted);
//        return decrypted;
    }
}
