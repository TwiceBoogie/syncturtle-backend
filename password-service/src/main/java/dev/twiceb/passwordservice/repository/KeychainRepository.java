package dev.twiceb.passwordservice.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

public interface KeychainRepository extends JpaRepository<Keychain, UUID> {

        @Query("SELECT CASE WHEN COUNT(kc) > 0 THEN TRUE ELSE FALSE END " +
                        "FROM Keychain kc " +
                        "WHERE kc.encryptionKey.user.id = :userId " +
                        "AND kc.domain = :domain")
        boolean CheckIfDomainExist(@Param("userId") UUID userId, @Param("domain") String domain);

        @Modifying
        @Query("UPDATE Keychain kc SET kc.favorite = :favorite WHERE kc.id = :passwordId")
        void updateFavoriteStatus(@Param("favorite") boolean favorite, @Param("passwordId") UUID passwordId);

        @Query("SELECT kc FROM Keychain kc WHERE kc.encryptionKey.user.id = :userId")
        List<Keychain> findAllKeychain(@Param("userId") UUID userId);

        @Query("SELECT kc FROM Keychain kc WHERE kc.encryptionKey.user.id = :userId")
        <T> Page<T> findAllByAccountId(@Param("userId") UUID userId, Pageable pageable, Class<T> clazz);

        @Query("SELECT kc FROM Keychain kc " +
                "WHERE kc.encryptionKey.user.id = :userId " +
                "AND (kc.status = 'SOON' OR kc.status = 'EXPIRED') " +
                "ORDER BY kc.createdAt ASC")
        <T> Page<T> getPasswordsExpiringSoon(@Param("userId") UUID userId, Pageable pageable, Class<T> clazz);

        @Query("SELECT kc FROM Keychain kc WHERE kc.encryptionKey.user.id = :userId ORDER BY kc.createdAt DESC")
        <T> Page<T> getRecentPasswords(@Param("userId") UUID userId, Pageable pageable, Class<T> clazz);

        @Query("SELECT kc FROM Keychain kc WHERE kc.encryptionKey.user.id = :userId AND kc.id = :id")
        Optional<DecryptedPasswordProjection> getPasswordById(@Param("userId") UUID userId, @Param("id") UUID id);

        @Query("SELECT kc FROM Keychain kc WHERE kc.domain ILIKE %:searchQuery% " +
                "AND kc.encryptionKey.user.id = :userId")
        Page<KeychainProjection> searchByQuery(
                @Param("searchQuery") String searchQuery, @Param("userId") UUID userId, Pageable pageable
        );

        @Query("SELECT kc FROM Keychain kc WHERE kc.id = :keychainId AND kc.encryptionKey.user.id = :userId")
        Optional<KeychainProjection> findKeychainById(@Param("keychainId") UUID keychainId, @Param("userId") UUID userId);

        @Query("SELECT kc FROM Keychain kc WHERE kc.status = :status")
        List<KeychainExpiringProjection> findAllKeychainsByStatus(@Param("status") DomainStatus status);

        @Query("SELECT kc FROM Keychain kc WHERE kc.notificationSent = false")
        List<KeychainNotificationProjection> findAllNotNotificationSent();

        @Modifying
        @Query("UPDATE Keychain kc SET kc.notificationSent = true WHERE kc.id IN :keychainIds")
        void updateMultiNotificationSent(@Param("keychainIds") List<UUID> keychainIds);

        @Modifying
        @Query("UPDATE Keychain kc SET kc.status = :status WHERE kc.id IN :keychainIds")
        void updateKeychainStatus(@Param("status") DomainStatus status, @Param("keychainIds") List<UUID> keychainIds);

        int countKeychainByEncryptionKey_User_Id(UUID userId);

}
