package dev.twiceb.passwordservice.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import dev.twiceb.passwordservice.model.EncryptionKey;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EncryptionKeyRepository extends JpaRepository<EncryptionKey, Long> {

    @Query("SELECT ek FROM EncryptionKey ek WHERE ek.user.id = :userId")
    EncryptionKey getEncryptionKeyId(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN SIZE(ek.keychains) = 0 THEN TRUE ELSE FALSE END " +
            "FROM EncryptionKey ek WHERE ek.user.id = :userId")
    boolean isPasswordVaultEmpty(@Param("userId") Long userId);

    @Query("SELECT ek FROM EncryptionKey ek WHERE ek.id = :id")
    <T> Optional<T> getEncryptionKeyById(@Param("id") Long id, Class<T> clazz);

    @Query("SELECT ek FROM EncryptionKey ek WHERE ek.user.id = :userId")
    <T> Optional<T> getEncryptionKeyByUserId(@Param("userId") Long userId, Class<T> clazz);

    @NotNull Page<EncryptionKey> findAll(@NotNull Pageable pageable);

    @Query("SELECT ek FROM EncryptionKey ek WHERE ek.expirationDate > :time AND ek.isEnabled = TRUE")
    Page<EncryptionKey> findAllByExpirationDateAfter(LocalDateTime time, Pageable pageable);

    @Query("SELECT COUNT(k) FROM EncryptionKey ek JOIN ek.keychains k WHERE ek.user.id = :userId")
    int countTotalKeychainsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(k) FROM EncryptionKey ek JOIN ek.keychains k " +
            "WHERE k.complexityMetric.passwordComplexityScore < 50 " +
            "AND ek.user.id = :userId")
    int countTotalWeakPasswords(@Param("userId") Long userId);

    @Query("SELECT SUM(k.complexityMetric.passwordComplexityScore) " +
            "FROM EncryptionKey ek " +
            "JOIN ek.keychains k " +
            "WHERE ek.user.id = :userId")
    Double sumPasswordComplexityScoreForUser(@Param("userId") Long userId);

}
