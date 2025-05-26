package dev.twiceb.passwordservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.twiceb.passwordservice.model.EncryptionKey;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EncryptionKeyRepository extends JpaRepository<EncryptionKey, UUID> {

        @Query("SELECT ek FROM EncryptionKey ek WHERE ek.user.id = :userId")
        EncryptionKey getEncryptionKeyId(@Param("userId") UUID userId);

        @Query("SELECT CASE WHEN SIZE(ek.keychains) = 0 THEN TRUE ELSE FALSE END " +
                        "FROM EncryptionKey ek WHERE ek.user.id = :userId")
        boolean isPasswordVaultEmpty(@Param("userId") UUID userId);

        @Query("SELECT ek FROM EncryptionKey ek WHERE ek.id = :id")
        <T> Optional<T> getEncryptionKeyById(@Param("id") UUID id, Class<T> clazz);

        @Query("SELECT ek FROM EncryptionKey ek WHERE ek.user.id = :userId")
        <T> Page<T> getEncryptionKeyByUserId(@Param("userId") UUID userId, Pageable pageable, Class<T> clazz);

        @Query("SELECT ek FROM EncryptionKey ek WHERE ek.expirationDate > :time AND ek.isEnabled = TRUE")
        Page<EncryptionKey> findAllByExpirationDateAfter(LocalDateTime time, Pageable pageable);

        @Query("SELECT COUNT(k) FROM EncryptionKey ek JOIN ek.keychains k WHERE ek.user.id = :userId")
        int countTotalKeychainsByUserId(@Param("userId") UUID userId);

        @Query("SELECT COUNT(k) FROM EncryptionKey ek JOIN ek.keychains k " +
                        "WHERE k.complexityMetric.passwordComplexityScore < 50 " +
                        "AND ek.user.id = :userId")
        int countTotalWeakPasswords(@Param("userId") UUID userId);

        @Query("SELECT SUM(k.complexityMetric.passwordComplexityScore) " +
                        "FROM EncryptionKey ek " +
                        "JOIN ek.keychains k " +
                        "WHERE ek.user.id = :userId")
        Double sumPasswordComplexityScoreForUser(@Param("userId") UUID userId);

}
