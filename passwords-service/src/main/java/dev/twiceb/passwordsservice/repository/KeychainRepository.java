package dev.twiceb.passwordsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.twiceb.passwordsservice.model.Keychain;

public interface KeychainRepository extends JpaRepository<Keychain, Long> {

}
