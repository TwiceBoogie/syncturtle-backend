package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.LoginAttempt;
import dev.twiceb.userservice.repository.projection.LoginAttemptProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    @Query("SELECT CASE WHEN COUNT(la) > 0 THEN TRUE ELSE FALSE END " +
            "FROM LoginAttempt la " +
            "WHERE la.success = true AND la.user.id = :userId AND la.attemptTimestamp > :timestamp")
    boolean isAttemptInResetDuration(@Param("userId") Long userId, @Param("timestamp") LocalDateTime timestamp);

    @Query("SELECT COUNT(la) FROM LoginAttempt la " +
            "WHERE la.success = false AND la.user.id = :userId AND la.attemptTimestamp > :timestamp")
    int countFailedAttempts(@Param("userId") Long userId, @Param("timestamp") LocalDateTime timestamp);

    @Query("SELECT la FROM LoginAttempt la WHERE la.user.id = :userId ORDER BY la.attemptTimestamp DESC LIMIT 1")
    Optional<LoginAttemptProjection> findRecentLoginAttempt(@Param("userId") Long userId);

    LoginAttempt findFirstByUserIdOrderByAttemptTimestampDesc(Long userId);
}
