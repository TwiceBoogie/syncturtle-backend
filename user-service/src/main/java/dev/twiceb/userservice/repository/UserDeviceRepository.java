package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.UserDevice;
import dev.twiceb.userservice.repository.projection.UserDeviceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {

    @Query("SELECT CASE WHEN COUNT(ud) > 0 THEN true ELSE false END " +
            "FROM UserDevice ud " +
            "WHERE ud.deviceKey = :deviceKey " +
            "AND ud.user.id = :userId")
    boolean existsByHashedDeviceKey(@Param("deviceKey") String deviceKey, @Param("userId") UUID userId);

    @Query("SELECT ud.id AS id, ud.deviceKey AS deviceKey, la.ipAddress AS ipAddress " +
            "FROM UserDevice ud " +
            "JOIN LoginAttempt la ON ud.user.id = la.user.id " +
            "AND ud.lastAccess = la.attemptTimestamp " +
            "WHERE ud.user.id = :userId " +
            "ORDER BY ud.lastAccess DESC")
    UserDeviceProjection getUserDevice(@Param("userId") UUID userId);
}
