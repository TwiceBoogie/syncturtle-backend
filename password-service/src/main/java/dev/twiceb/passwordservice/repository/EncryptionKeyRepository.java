package dev.twiceb.passwordsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.twiceb.passwordsservice.model.EncryptionKey;

public interface EncryptionKeyRepository extends JpaRepository<EncryptionKey, Long> {

}
