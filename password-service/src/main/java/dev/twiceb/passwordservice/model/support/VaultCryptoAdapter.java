package dev.twiceb.passwordservice.model.support;

import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.Ciphertext;
import org.springframework.vault.support.Plaintext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VaultCryptoAdapter implements CryptoPort {

    private final VaultOperations ops;
    private final String backend;

    @Override
    public String encrypt(String keyName, String plainText) {
        return ops.opsForTransit(backend).encrypt(keyName, Plaintext.of(plainText)).getCiphertext();
    }

    @Override
    public String decrypt(String keyName, String cypherText) {
        return ops.opsForTransit(backend).decrypt(keyName, Ciphertext.of(cypherText)).asString();
    }

}
