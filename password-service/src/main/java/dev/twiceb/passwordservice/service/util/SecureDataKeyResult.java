package dev.twiceb.passwordservice.service.util;

import dev.twiceb.passwordservice.model.EncryptionKey;
import dev.twiceb.passwordservice.model.Keychain;
import lombok.Data;

@Data
public class SecureDataKeyResult {

    private EncryptionKey encryptionKey;
    private Keychain keychain;
}
