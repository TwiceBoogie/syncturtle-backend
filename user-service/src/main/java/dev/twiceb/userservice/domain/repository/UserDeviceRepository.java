package dev.twiceb.userservice.domain.repository;

import dev.twiceb.userservice.domain.model.UserDevice;
import dev.twiceb.userservice.domain.projection.UserDeviceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {

    @Query("SELECT CASE WHEN COUNT(ud) > 0 THEN true ELSE false END " + "FROM UserDevice ud "
            + "WHERE ud.deviceKey = :deviceKey " + "AND ud.user.id = :userId")
    boolean existsByHashedDeviceKey(@Param("deviceKey") String deviceKey,
            @Param("userId") UUID userId);

    @Query("SELECT ud.id AS id, ud.deviceKey AS deviceKey, l.ipAddress AS ipAddress "
            + "FROM UserDevice ud " + "JOIN Login l ON ud.user.id = l.user.id "
            + "AND ud.lastAccess = l.attemptTimestamp " + "WHERE ud.user.id = :userId "
            + "ORDER BY ud.lastAccess DESC")
    UserDeviceProjection getUserDevice(@Param("userId") UUID userId);
}
