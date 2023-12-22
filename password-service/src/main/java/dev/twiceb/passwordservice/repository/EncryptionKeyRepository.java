package dev.twiceb.passwordservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.twiceb.passwordservice.model.EncryptionKey;

public interface EncryptionKeyRepository extends JpaRepository<EncryptionKey, Long> {

}
