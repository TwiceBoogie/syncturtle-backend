package dev.twiceb.userservice.repository;

import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.projection.AuthUserProjection;
import dev.twiceb.userservice.repository.projection.UserDeviceProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT user FROM User user WHERE user.email = :email")
    <T> Optional<T> getUserByEmail(@Param("email") String email, Class<T> type);

    @Query("SELECT user FROM User user WHERE user.id = :userId")
    <T> Optional<T> getUserById(@Param("userId") Long userId, Class<T> clazz);

    Optional<User> findUserByUsername(String username);

    @Query("SELECT user FROM User user WHERE user.id = :userId")
    AuthUserProjection getAuthUserProjection(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN count(user) > 0 THEN true ELSE false END FROM User user WHERE user.id = :userId")
    boolean isUserExist(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User user SET user.userStatus = :userStatus WHERE user.id = :userId")
    void updateUserStatus(@Param("userStatus") UserStatus userStatus, @Param("userId") Long userId);

    @Query("SELECT CASE WHEN count(user) > 0 THEN true ELSE false END FROM User user WHERE user.email = :email")
    boolean isUserExistByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE User user SET user.verified = true WHERE user.id = :userId")
    void updateActiveUserProfile(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User user SET user.password = :password WHERE user.id = :userId")
    void updatePassword(@Param("password") String password, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User user SET user.email = :email WHERE user.id = :userId")
    void updateEmail(@Param("email") String email, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User user SET user.countryCode = :countryCode, user.phone = :phone WHERE user.id = :userId")
    void updatePhone(@Param("countryCode") String countryCode, @Param("phone") Long phone,
            @Param("userId") Long userId);

    @Query("SELECT user.email FROM User user WHERE user.id = :userId")
    String getUserEmail(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User user SET user.notificationCount = user.notificationCount + 1 WHERE user.id = :userId")
    void increaseNotificationCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User user SET user.notificationCount = CASE WHEN user.notificationCount > 0 " +
            "THEN user.notificationCount - 1 ELSE user.notificationCount END WHERE user.id = :userId")
    void decreaseNotificationCount(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User user SET user.notificationCount = 0 WHERE user.id = :userId")
    void resetNotificationCount(@Param("userId") Long userId);

    @Query("SELECT COUNT(user) FROM User user WHERE user.createdDate > :timePeriod")
    int countUsersByTimePeriod(@Param("timePeriod") LocalDateTime timePeriod);

    @Query("SELECT COUNT(user) FROM User user WHERE user.createdDate > :timePeriod AND user.verified = TRUE")
    int countVerifiedUsersByTimePeriod(@Param("timePeriod") LocalDateTime timePeriod);

    boolean existsUserByUsername(String username);

    @Modifying
    @Query("UPDATE User user SET user.gender = :gender WHERE user.id = :userId")
    void updateGender(@Param("gender") String gender, @Param("userId") Long userId);

    // @Query("SELECT u.id AS userId, ud.id AS userDeviceId, ud.deviceKey AS
    // deviceKey, la.ipAddress AS ipAddress " +
    // "FROM User u " +
    // "LEFT JOIN u.userDevices ud " +
    // "LEFT JOIN u.loginAttempts la " +
    // "ON ud.lastAccess = (SELECT MAX(ud2.lastAccess) FROM UserDevice ud2 WHERE
    // ud2.user = u) " +
    // "AND la.attemptTimestamp = (SELECT MAX(la2.attemptTimestamp) FROM
    // LoginAttempt la2 WHERE la2.user = u) " +
    // "WHERE u.id = :userId")
    // UserDeviceProjection getUserDevice(@Param("userId") Long userId);
}
