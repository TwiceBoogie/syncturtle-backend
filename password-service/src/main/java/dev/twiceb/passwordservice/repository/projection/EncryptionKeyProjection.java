package dev.twiceb.passwordservice.repository.projection;

public interface EncryptionKeyProjection {
    String getDek();

    byte[] getVector();
}
