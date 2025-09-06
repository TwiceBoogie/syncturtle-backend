package dev.twiceb.userservice.domain.repository;

import dev.twiceb.userservice.domain.model.Login;
import dev.twiceb.userservice.domain.projection.LoginAttemptProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface LoginRepository extends JpaRepository<Login, UUID> {

    @Query("SELECT la FROM Login la WHERE la.user.id = :userId ORDER BY la.attemptTimestamp DESC LIMIT 1")
    Optional<LoginAttemptProjection> findRecentLogin(@Param("userId") UUID userId);

    Login findFirstByUserIdOrderByAttemptTimestampDesc(UUID userId);

    @Query("""
            SELECT COUNT(l)
            FROM Login l
            WHERE l.user.id = :userId
            AND l.success = false
            AND l.attemptTimestamp >= :start
            """)
    int countFailedByUserSince(@Param("userId") UUID userId, @Param("start") Instant start);

    @Query("""
            SELECT COUNT(l)
            FROM Login l
            WHERE l.user = NULL
            AND l.ipAddress = :ip
            AND l.success = false
            AND l.attemptTimestamp >= :start
            """)
    int countFailedByIpSince(@Param("ip") String ip, @Param("start") Instant start);

    @Query("""
            SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
            FROM Login l
            WHERE l.user.id = :userId
            AND l.success = true
            AND l.attemptTimestamp >= :start
            """)
    boolean existsRecentSuccessByUser(@Param("userId") UUID userId, @Param("start") Instant start);

    @Query("""
            SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END
            FROM Login l
            WHERE l.ipAddress = :ip
            AND l.success = false
            AND l.attemptTimestamp >= :start
            """)
    boolean existsRecentSuccessByIp(@Param("ip") String ipAddress, @Param("start") Instant start);
}
