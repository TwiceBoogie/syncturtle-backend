package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.enums.ActivationCodeType;
import dev.twiceb.userservice.model.ActivationCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivationCodeRepository extends JpaRepository<ActivationCode, Long> {
    @Modifying
    @Query("UPDATE ActivationCode ac SET ac.hashedCode = :activationCode WHERE ac.user.id = :userId")
    int updateActivationCodeByUserId(@Param("activationCode") String activationCode, @Param("userId") Long userId);

    @Query("SELECT ac FROM ActivationCode ac WHERE ac.hashedCode = :hashedCode")
    <T> Optional<T> getActivationCodeByHashedCode(@Param("hashedCode") String hashedCode, Class<T> type);

    @Query("SELECT ac FROM ActivationCode ac WHERE ac.codeType = :codeType AND ac.expirationTime >= :timestamp")
    List<ActivationCode> getActivationCodeByCodeType(@Param("codeType") ActivationCodeType codeType,
                                                     @Param("timestamp")LocalDateTime timestamp);
}
