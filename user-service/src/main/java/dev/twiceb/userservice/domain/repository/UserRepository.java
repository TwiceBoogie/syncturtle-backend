package dev.twiceb.userservice.domain.repository;

import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.userservice.domain.model.User;
import dev.twiceb.userservice.domain.projection.AuthUserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<UUID> findIdByEmailIgnoreCase(String email);

    @Query("SELECT user FROM User user WHERE user.email = :email")
    <T> Optional<T> getUserByEmail(@Param("email") String email, Class<T> type);

    @Query("SELECT user FROM User user WHERE user.id = :userId")
    <T> Optional<T> getUserById(@Param("userId") UUID userId, Class<T> clazz);

    Optional<User> findUserByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT user FROM User user WHERE user.id = :userId")
    AuthUserProjection getAuthUserProjection(@Param("userId") UUID userId);

    @Query("SELECT CASE WHEN count(user) > 0 THEN true ELSE false END FROM User user WHERE user.id = :userId")
    boolean isUserExist(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User user SET user.userStatus = :userStatus WHERE user.id = :userId")
    void updateUserStatus(@Param("userStatus") UserStatus userStatus, @Param("userId") UUID userId);

    @Query("SELECT CASE WHEN count(user) > 0 THEN true ELSE false END FROM User user WHERE user.email = :email")
    boolean isUserExistByEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User user SET user.password = :password WHERE user.id = :userId")
    void updatePassword(@Param("password") String password, @Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User user SET user.email = :email WHERE user.id = :userId")
    void updateEmail(@Param("email") String email, @Param("userId") UUID userId);

    @Query("SELECT user.email FROM User user WHERE user.id = :userId")
    String getUserEmail(@Param("userId") UUID userId);

    UUID findIdByEmail(String email);

    @Modifying
    @Query("UPDATE User user SET user.notificationCount = user.notificationCount + 1 WHERE user.id = :userId")
    void increaseNotificationCount(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User user SET user.notificationCount = CASE WHEN user.notificationCount > 0 "
            + "THEN user.notificationCount - 1 ELSE user.notificationCount END WHERE user.id = :userId")
    void decreaseNotificationCount(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE User user SET user.notificationCount = 0 WHERE user.id = :userId")
    void resetNotificationCount(@Param("userId") UUID userId);

    @Query("SELECT COUNT(user) FROM User user WHERE user.createdAt > :since")
    int countUsersByTimePeriod(@Param("since") Instant since);

    @Query("SELECT COUNT(user) FROM User user WHERE user.createdAt > :since AND user.active = TRUE")
    int countVerifiedUsersByTimePeriod(@Param("since") Instant since);

    boolean existsUserByUsername(String username);
}
