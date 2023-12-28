package dev.twiceb.passwordservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.twiceb.passwordservice.model.Keychain;
import dev.twiceb.passwordservice.repository.projection.DecryptedPasswordProjection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeychainRepository extends JpaRepository<Keychain, Long> {

    @Query("SELECT CASE WHEN COUNT(kc) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Keychain kc " +
            "WHERE kc.account.id = :accountId " +
            "AND kc.domain = :domain")
    boolean CheckIfDomainExist(@Param("accountId") Long accountId, @Param("domain") String domain);

    @Query("SELECT kc FROM Keychain kc WHERE kc.accountId = :accountId")
    <T> Page<T> findAllByAccountId(@Param("accountId") Long accountId, Pageable pageable, Class<T> clazz);

    @Query("SELECT kc FROM Keychain kc WHERE kc.accountId = :accountId AND (kc.status = 'SOON' OR kc.status = 'EXPIRED') ORDER BY kc.date ASC")
    <T> Page<T> getPasswordsExpiringSoon(@Param("accountId") Long accountId, Pageable pageable, Class<T> clazz);

    @Query("SELECT kc FROM Keychain kc WHERE kc.accountId = :accountId ORDER BY kc.date DESC")
    <T> Page<T> getRecentPasswords(@Param("accountId") Long accountId, Pageable pageable, Class<T> clazz);

    @Query("SELECT kc FROM Keychain kc WHERE kc.accountId = :accountId AND kc.domain = :domain")
    <T> Optional<T> getPasswordByDomain(@Param("accountId") Long accountId, @Param("domain") String domain,
            Class<T> clazz);

    @Query("SELECT kc FROM Keychain kc WHERE kc.accountId = :accountId AND kc.id = :id")
    DecryptedPasswordProjection getPasswordById(@Param("accountId") Long accountId, @Param("id") Long id);
}
