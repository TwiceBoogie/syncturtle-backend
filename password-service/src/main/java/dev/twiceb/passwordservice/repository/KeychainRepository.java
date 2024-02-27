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
                        "WHERE kc.encryptionKey.user.id = :userId " +
                        "AND kc.domain = :domain")
        boolean CheckIfDomainExist(@Param("userId") Long userId, @Param("domain") String domain);

        @Modifying
        @Query("UPDATE Keychain kc SET kc.favorite = :favorite WHERE kc.id = :passwordId")
        void updateFavoriteStatus(@Param("favorite") boolean favorite, @Param("passwordId") Long passwordId);

        @Query("SELECT kc FROM Keychain kc WHERE kc.encryptionKey.user.id = :userId")
        List<Keychain> findAllKeychain(@Param("userId") Long userId);

        @Query("SELECT kc FROM Keychain kc WHERE kc.encryptionKey.user.id = :userId")
        <T> Page<T> findAllByAccountId(@Param("userId") Long userId, Pageable pageable, Class<T> clazz);

        @Query("SELECT kc FROM Keychain kc " +
                "WHERE kc.encryptionKey.user.id = :userId " +
                "AND (kc.status = 'SOON' OR kc.status = 'EXPIRED') " +
                "ORDER BY kc.createdAt ASC")
        <T> Page<T> getPasswordsExpiringSoon(@Param("userId") Long userId, Pageable pageable, Class<T> clazz);

        @Query("SELECT kc FROM Keychain kc WHERE kc.encryptionKey.user.id = :userId ORDER BY kc.createdAt DESC")
        <T> Page<T> getRecentPasswords(@Param("userId") Long userId, Pageable pageable, Class<T> clazz);

        @Query("SELECT kc FROM Keychain kc WHERE kc.encryptionKey.user.id = :userId AND kc.id = :id")
        DecryptedPasswordProjection getPasswordById(@Param("userId") Long userId, @Param("id") Long id);

        @Query("SELECT kc FROM Keychain kc WHERE kc.domain ILIKE %:searchQuery% " +
                "AND kc.encryptionKey.user.id = :userId")
        Page<KeychainProjection> searchByQuery(
                @Param("searchQuery") String searchQuery, @Param("userId") Long userId, Pageable pageable
        );

        @Query("SELECT kc FROM Keychain kc WHERE kc.id = :keychainId")
        KeychainProjection findKeychainById(@Param("keychainId") Long keychainId);

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

        int countKeychainByEncryptionKey_User_Id(Long userId);

}
