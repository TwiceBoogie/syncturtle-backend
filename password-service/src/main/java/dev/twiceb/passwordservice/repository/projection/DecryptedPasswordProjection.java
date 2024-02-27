package dev.twiceb.passwordservice.repository.projection;

public interface DecryptedPasswordProjection {
    Long getId();
    byte[] getPassword();
    byte[] getVector();
    EncryptionKeyProjection getEncryptionKey();
}
