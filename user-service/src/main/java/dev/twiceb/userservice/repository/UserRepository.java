package dev.twiceb.userservice.repository;

import dev.twiceb.userservice.model.User;
import dev.twiceb.userservice.repository.projection.UserPrincipalProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT user FROM User user WHERE user.email = :email")
    <T> Optional<T> getUserByEmail(@Param("email") String email, Class<T> type);

    @Query("SELECT CASE WHEN count(user) > 0 THEN true ELSE false END FROM User user WHERE user.id = :userId")
    boolean isUserExist(@Param("userId") Long userId);

    @Query("SELECT CASE WHEN count(user) > 0 THEN true ELSE false END FROM User user WHERE user.email = :email")
    boolean isUserExistByEmail(@Param("email") String email);

//    @Query("SELECT user.activationCode FROM User user WHERE user.id = :userId")
//    String getActivationCode(@Param("userId") Long userId);

//    @Query("SELECT user From User user WHERE user.activationCode = :code")
//    Optional<UserPrincipalProjection> getUserByActivationCode(@Param("code") String code);

    @Modifying
    @Query("UPDATE User user SET user.active = true WHERE user.id = :userId")
    void updateActiveUserProfile(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE User user SET user.password = :password WHERE user.id = :userId")
    void updatePassword(@Param("password") String password, @Param("userId") Long userId);
}
