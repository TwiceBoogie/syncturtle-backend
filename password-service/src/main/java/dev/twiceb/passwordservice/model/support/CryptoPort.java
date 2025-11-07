package dev.twiceb.passwordservice.model.support;

public interface CryptoPort {
    String encrypt(String keyName, String plainText);

    String decrypt(String keyName, String cypherText);
}
