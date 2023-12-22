package dev.twiceb.passwordservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.twiceb.passwordservice.model.Keychain;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeychainRepository extends JpaRepository<Keychain, Long> {

    @Query("SELECT CASE WHEN COUNT(kc) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Keychain kc " +
            "WHERE kc.account.id = :accountId " +
            "AND kc.domain = :domain")
    boolean CheckIfDomainExist(@Param("accountId") Long accountId, @Param("domain") String domain);
}
