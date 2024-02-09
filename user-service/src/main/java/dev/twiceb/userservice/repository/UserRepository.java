package dev.twiceb.userservice.repository;

import dev.twiceb.common.enums.UserStatus;
import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.projection.AuthUserProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT user FROM User user WHERE user.email = :email")
    <T> Optional<T> getUserByEmail(@Param("email") String email, Class<T> type);

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
}
