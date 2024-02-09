package dev.twiceb.passwordservice.repository;

import java.util.List;

import dev.twiceb.passwordservice.enums.DomainStatus;
import dev.twiceb.passwordservice.repository.projection.KeychainExpiringProjection;
import dev.twiceb.passwordservice.repository.projection.KeychainNotificationProjection;
import dev.twiceb.passwordservice.repository.projection.KeychainProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import dev.twiceb.passwordservice.model.Keychain;
import dev.twiceb.passwordservice.repository.projection.DecryptedPasswordProjection;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KeychainRepository extends JpaRepository<Keychain, Long> {

        @Query("SELECT CASE WHEN COUNT(kc) > 0 THEN TRUE ELSE FALSE END " +
                        "FROM Keychain kc " +
                        "WHERE kc.account.id = :accountId " +
                        "AND kc.domain = :domain")
        boolean CheckIfDomainExist(@Param("accountId") Long accountId, @Param("domain") String domain);

        @Query("SELECT kc FROM Keychain kc WHERE kc.account.id = :accountId")
        List<Keychain> findAllKeychain(@Param("accountId") Long accountId);

        @Query("SELECT kc FROM Keychain kc WHERE kc.account.id = :accountId")
        <T> Page<T> findAllByAccountId(@Param("accountId") Long accountId, Pageable pageable, Class<T> clazz);

        @Query("SELECT kc FROM Keychain kc WHERE kc.account.id = :accountId AND (kc.status = 'SOON' OR kc.status = 'EXPIRED') ORDER BY kc.createdAt ASC")
        <T> Page<T> getPasswordsExpiringSoon(@Param("accountId") Long accountId, Pageable pageable, Class<T> clazz);

        @Query("SELECT kc FROM Keychain kc WHERE kc.account.id = :accountId ORDER BY kc.createdAt DESC")
        <T> Page<T> getRecentPasswords(@Param("accountId") Long accountId, Pageable pageable, Class<T> clazz);

        @Query("SELECT kc FROM Keychain kc WHERE kc.account.id = :accountId AND kc.id = :id")
        DecryptedPasswordProjection getPasswordById(@Param("accountId") Long accountId, @Param("id") Long id);

        @Query("SELECT kc FROM Keychain kc WHERE kc.domain ILIKE %:searchQuery% " +
                "AND kc.account.id = :accountId")
        Page<KeychainProjection> searchByQuery(
                @Param("searchQuery") String searchQuery, @Param("accountId") Long accountId, Pageable pageable
        );

        @Query("SELECT kc FROM Keychain kc WHERE kc.status = :status")
        List<KeychainExpiringProjection> findAllKeychainsByStatus(@Param("status") DomainStatus status);

        @Query("SELECT kc FROM Keychain kc WHERE kc.notificationSent = false")
        List<KeychainNotificationProjection> findAllNotNotificationSent();

        @Modifying
        @Query("UPDATE Keychain kc SET kc.notificationSent = true WHERE kc.id IN :keychainIds")
        void updateMultiNotificationSent(@Param("keychainIds") List<Long> keychainIds);

        @Modifying
        @Query("UPDATE Keychain kc SET kc.status = :status WHERE kc.id IN :keychainIds")
        void updateKeychainStatus(@Param("status") DomainStatus status, @Param("keychainIds") List<Long> keychainIds);

}
