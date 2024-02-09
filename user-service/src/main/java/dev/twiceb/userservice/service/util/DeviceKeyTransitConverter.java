package dev.twiceb.userservice.service.util;

import dev.twiceb.common.util.BeanUtil;
import jakarta.persistence.AttributeConverter;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Plaintext;

public class DeviceKeyTransitConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        VaultOperations vaultOps = BeanUtil.getBean(VaultOperations.class);
        Plaintext plaintext = Plaintext.of(attribute);
        return vaultOps.opsForTransit().encrypt("deviceKey", plaintext).getCiphertext();
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        VaultOperations vaultOps = BeanUtil.getBean(VaultOperations.class);
        Ciphertext ciphertext = Ciphertext.of(dbData);
        return vaultOps.opsForTransit().decrypt("deviceKey", ciphertext).asString();
    }
}
