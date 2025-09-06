package dev.twiceb.userservice.domain.repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import dev.twiceb.userservice.domain.model.LockedUser;

public interface LockedUserRepository extends JpaRepository<LockedUser, Long> {

    LockedUser findFirstByUserIdOrderByCreatedAtDesc(UUID userId);

    boolean existsByUserIdAndLockoutEndAfter(UUID userId, Instant now);

    @Query("""
            SELECT l FROM LockedUser l
            WHERE l.user.id = :userId AND l.lockoutEnd > :now
            """)
    Optional<LockedUser> findActiveLock(@Param("userId") UUID userId, @Param("now") Instant now);

    @Modifying
    @Query("""
            UPDATE LockedUser l
            SET l.failedDuringLockCount = l.failedDuringLockCount + 1,
                l.lastFailedAttempt = :now
            WHERE l.user.id = :userId AND l.lockoutEnd > :now
            """)
    int bumpFailedDuringLock(@Param("userId") UUID userId, @Param("now") Instant now);

}
